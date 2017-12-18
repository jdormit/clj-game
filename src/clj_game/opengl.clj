(ns clj-game.opengl
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
