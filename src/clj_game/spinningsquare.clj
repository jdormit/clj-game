(ns clj-game.spinningsquare
  (:require [clj-game.opengl :as opengl])
  (:import (org.lwjgl.opengl GL11 GL30 GL20)))

(declare vertex-shader fragment-shader)

(defn setup []
  (GL30/glBindVertexArray (GL30/glGenVertexArrays))
  (let [vertices [-0.5  0.5  1.0  0.0  0.0
                   0.5  0.5  0.0  1.0  0.0
                   0.5 -0.5  0.0  0.0  1.0
                  -0.5 -0.5  1.0  1.0  0.0]
        indices [0 1 2
                 2 3 0]
        angle 0.0
        vertex-buffer (opengl/gen-buffer :array-buffer)
        indices-buffer (opengl/gen-buffer :element-array-buffer)
        program (opengl/link-shaders [(vertex-shader)
                                      (fragment-shader)])
        angle-uniform (opengl/get-uniform program "angle")]
    (GL20/glUseProgram program)
    (opengl/load-data vertices :array-buffer :float :static-draw)
    (opengl/load-data indices :element-array-buffer :byte :static-draw)
    (opengl/set-uniform angle-uniform :float angle)
    (opengl/link-attribute program "position" 2 :float false (* 5 Float/BYTES) 0)
    (opengl/link-attribute program "color" 3 :float false (* 5 Float/BYTES) (* 2 Float/BYTES))
    {:angle angle
     :angle-uniform angle-uniform}))

(defn update-state [state]
  (assoc state :angle (mod (+ (:angle state) 1) 360)))

(defn render [state]
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (opengl/set-uniform (:angle-uniform state) :float (:angle state))
  (GL11/glDrawElements GL11/GL_TRIANGLES 6 GL11/GL_UNSIGNED_BYTE 0))

(def vertex-shader
  (opengl/make-shader
   :vertex-shader
   "#version 150 core

    uniform float angle;

    in vec2 position;
    in vec3 color;

    out vec3 Color;

    void main () {
        float angle_radians = angle * (3.1415926535/180);
        mat2 rotation_matrix = mat2(cos(angle_radians), -sin(angle_radians),
                                    sin(angle_radians), cos(angle_radians));
        gl_Position = mat4(rotation_matrix) * vec4(position, 0.0, 1.0);
        Color = color;
    }"))

(def fragment-shader
  (opengl/make-shader
   :fragment-shader
   "#version 150 core

    in vec3 Color;

    out vec4 outColor;

    void main() {
        outColor = vec4(Color, 1.0);
    }"))
