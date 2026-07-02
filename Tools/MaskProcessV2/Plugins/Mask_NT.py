#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Author        : XuHaoNan

import typing

RGBA = typing.NamedTuple("RGBA", [("r", int), ("g", int), ("b", int), ("a", int)])
POS = typing.NamedTuple("POS", [("x", int), ("y", int)])
PROGRAM_IMAGE_VARIABLE = dict[str, any]
MASK_CONDITION = typing.Callable[[RGBA, POS, PROGRAM_IMAGE_VARIABLE], typing.Optional[RGBA]]
MASK_APPLY = typing.Callable[[RGBA, POS, PROGRAM_IMAGE_VARIABLE], typing.Optional[RGBA]]
MASK_PROGRAM_LINE = typing.NamedTuple("MASK_PROGRAM_LINE", [("LineName", str), ("MaskCondition", str), ("MaskApply", str)])
MASK_PROGRAM = typing.NamedTuple("MASK_PROGRAM", [("ProgramVariable", typing.Callable[[], PROGRAM_IMAGE_VARIABLE]), ("Program", list[MASK_PROGRAM_LINE]), ("AllowSplit", bool), ("AllowMerge", bool)])
MaskProgramRegistry = tuple[dict[str, MASK_CONDITION], dict[str, MASK_APPLY], dict[str, MASK_PROGRAM]]

LayerColor = RGBA(255, 0, 0, 255)  # (R, G, B, A)


def IsSameColor(Color1: RGBA, Color2: RGBA) -> bool:
	return Color1.r == Color2.r and Color1.g == Color2.g and Color1.b == Color2.b and Color1.a == Color2.a

MaskNTCondition_R = lambda Color, PixelPos, Variable: LayerColor if Color[0] == 255 and Color[3] == 255 else None
MaskNTApply_R = lambda Color, PixelPos, Variable: (0, 0, 0, 255) if IsSameColor(Color, LayerColor) else None
MaskNT_R: MASK_PROGRAM = MASK_PROGRAM(
	lambda: {},
	[
		MASK_PROGRAM_LINE("primaryColor", "default:primaryColor", "default:primaryColor"),
		MASK_PROGRAM_LINE("accentColor1", "default:accentColor1", "default:accentColor1"),
		MASK_PROGRAM_LINE("accentColor2", "default:accentColor2", "default:accentColor2"),
		MASK_PROGRAM_LINE("eyeColorA", "default:eyeColorA", "default:eyeColorA"),
		MASK_PROGRAM_LINE("eyeColorB", "default:eyeColorB", "default:eyeColorB"),
		MASK_PROGRAM_LINE("maskNewTextureR", "mask_nt:mask_nt_r", "mask_nt:mask_nt_r")
	],
	False, True
)

def registryAllMaskProgram() -> MaskProgramRegistry:
	C, A, P = {}, {}, {}
	C["mask_nt:mask_nt_r"] = MaskNTCondition_R
	A["mask_nt:mask_nt_r"] = MaskNTApply_R
	P["mask_nt:mask_nt_r"] = MaskNT_R
	return C, A, P