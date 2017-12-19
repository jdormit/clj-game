(ns clj-game.shaders
  (:require [clj-game.opengl :as opengl]))

(def basic-vertex-shader
  (opengl/make-shader
   :vertex-shader
   "#version 150 core

    in vec2 position;
    in vec3 color;

    out vec3 Color;

    void main () {
        gl_Position = vec4(position, 0.0, 1.0);
        Color = color;
    }"))

(def basic-fragment-shader
  (opengl/make-shader
   :fragment-shader
   "#version 150 core

    in vec3 Color;

    out vec4 outColor;

    void main() {
        outColor = vec4(Color, 1.0);
    }"))
