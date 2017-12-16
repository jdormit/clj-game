(ns clj-game.shaders
  (:import (org.lwjgl.opengl GL20)))

(defn make-shader
  "Creates and compiles a new shader"
  [type source]
  (fn []
    (let [shader (GL20/glCreateShader type)]
      (GL20/glShaderSource shader source)
      (GL20/glCompileShader shader)
      (if (= (GL20/glGetShaderi shader GL20/GL_COMPILE_STATUS) 1)
        shader
        (throw (RuntimeException. (GL20/glGetShaderInfoLog shader)))))))

(def basic-vertex-shader
  (make-shader GL20/GL_VERTEX_SHADER
               "#version 150 core

                in vec2 position;
                in vec3 color;

                out vec3 Color;

                void main () {
                  gl_Position = vec4(position, 0.0, 1.0);
                  Color = color;
                }"))

(def basic-fragment-shader
  (make-shader GL20/GL_FRAGMENT_SHADER
               "#version 150 core

                in vec3 Color;

                out vec4 outColor;

                void main() {
                  outColor = vec4(Color, 1.0);
                }"))
