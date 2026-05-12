#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# Project       : MergePicture
# File          : MergePicture.py
# Author        : XuHaoNan
# Version       : 1.0.0

import os
import typing
from PIL import Image


def readImage(Path: str) -> Image.Image:
	ImageData = Image.open(Path, "r", ["png"])
	if ImageData.mode != "RGBA":
		print("[ERROR] %s 的图片格式不是 RGBA" % Path)
		ImageData = ImageData.convert("RGBA")
	return ImageData


def MergePicture(PictureA: Image.Image, PictureB: Image.Image, processFunc: typing.Callable[[tuple[int, int, int, int], tuple[int, int, int, int]], tuple[int, int, int, int]]) -> Image.Image:
	FinalImage = Image.new("RGBA", (PictureA.width, PictureA.height))
	for x in range(PictureA.width):
		for y in range(PictureA.height):
			PixelA = PictureA.getpixel((x, y))
			PixelB = PictureB.getpixel((x, y))
			FinalImage.putpixel((x, y), processFunc(PixelA, PixelB))
	return FinalImage


if __name__ == "__main__":
	PictureA = readImage("./water_flow_c.png")
	PictureB = readImage("./water_flow.png")
	FinalImage = MergePicture(PictureA, PictureB, lambda PixelA, PixelB: (PixelA[0], PixelA[1], PixelA[2], PixelB[3]))
	FinalImage.save("./water_flow_f.png")