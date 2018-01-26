(ns clj-game.basictexture
  (:require [clj-game.opengl :as opengl])
  (:import (org.lwjgl.opengl GL11 GL20 GL30 GL12)))

(declare vertex-shader fragment-shader)

(defn setup []
  (GL30/glBindVertexArray (GL30/glGenVertexArrays))
  (let [texture-pixels [0.0 0.0 0.0  1.0 1.0 1.0
                        1.0 1.0 1.0  0.0 0.0 0.0]
        ;         position   texture coordinates
        vertices [-0.5  0.5  0.0 0.0
                   0.5  0.5  1.0 0.0
                   0.5 -0.5  1.0 1.0
                  -0.5 -0.5  0.0 1.0]
        indices [0 1 2
                 2 3 0]
        vertex-buffer (opengl/gen-buffer :array-buffer)
        indices-buffer (opengl/gen-buffer :element-array-buffer)
        program (opengl/link-shaders [(vertex-shader)
                                      (fragment-shader)])
        texture-ref (GL11/glGenTextures)]
    (GL20/glUseProgram program)
    (opengl/load-data vertices :array-buffer :float :static-draw)
    (opengl/load-data indices :element-array-buffer :byte :static-draw)
    (opengl/link-attribute program "position" 2 :float false (* 4 Float/BYTES) 0)
    (opengl/link-attribute program "texcoord" 2 :float false (* 4 Float/BYTES) (* 2 Float/BYTES))
    ;; TODO abstract texture stuff into clj-game.opengl functions
    ;; Maybe store data in a 2D vector to avoid having to specify width/height?
    (GL11/glBindTexture GL11/GL_TEXTURE_2D texture-ref)
    (GL11/glTexImage2D GL11/GL_TEXTURE_2D 0 GL11/GL_RGB 2 2 0 GL11/GL_RGB GL11/GL_FLOAT (float-array texture-pixels))
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D
                          GL11/GL_TEXTURE_WRAP_S
                          GL12/GL_CLAMP_TO_EDGE)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D
                          GL11/GL_TEXTURE_WRAP_T
                          GL12/GL_CLAMP_TO_EDGE)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D
                          GL11/GL_TEXTURE_MIN_FILTER
                          GL11/GL_NEAREST)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D
                          GL11/GL_TEXTURE_MAG_FILTER
                          GL11/GL_NEAREST)
    {}))

(defn update-state [state]
  state)

(defn render [state]
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (GL11/glDrawElements GL11/GL_TRIANGLES 6 GL11/GL_UNSIGNED_BYTE 0))

(def vertex-shader
  (opengl/make-shader
    :vertex-shader
    "#version 150 core

    in vec2 position;
    in vec2 texcoord;

    out vec2 Texcoord;

    void main () {
        gl_Position = vec4(position, 0.0, 1.0);
        Texcoord = texcoord;
    }"))

(def fragment-shader
  (opengl/make-shader
    :fragment-shader
    "#version 150 core

    in vec2 Texcoord;

    out vec4 outColor;

    uniform sampler2D tex;

    void main () {
        outColor = texture(tex, Texcoord);
    }"))
