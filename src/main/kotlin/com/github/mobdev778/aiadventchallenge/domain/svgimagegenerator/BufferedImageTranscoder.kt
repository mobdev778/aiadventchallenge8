package com.github.mobdev778.aiadventchallenge.domain.svgimagegenerator

import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import java.awt.image.BufferedImage

class BufferedImageTranscoder : ImageTranscoder() {
    var bufferedImage: BufferedImage? = null
        private set

    override fun createImage(width: Int, height: Int): BufferedImage {
        return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }

    override fun writeImage(img: BufferedImage, output: TranscoderOutput?) {
        this.bufferedImage = img
    }
}