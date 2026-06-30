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

AllayGlowCondition = lambda Color, PixelPos, Variable: RGBA(Color.r, Color.g, Color.b, int(Color.a / 2))
AllayGlowProgram: MASK_PROGRAM = MASK_PROGRAM(
	lambda: {},
	[
		MASK_PROGRAM_LINE("output", "allay_glow:half_a", ""),
	],
	True, False
)

def registryAllMaskProgram() -> MaskProgramRegistry:
	C, A, P = {}, {}, {}
	C["allay_glow:half_a"] = AllayGlowCondition
	P["allay_glow:half_a"] = AllayGlowProgram
	return C, A, P