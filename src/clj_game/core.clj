(ns clj-game.core
  (:require [clj-game.shaders :as shaders]
            [clj-game.opengl :as opengl])
  (:import (org.lwjgl.glfw GLFW GLFWErrorCallback)
           (org.lwjgl.opengl GL GL11 GL15 GL20 GL30)
           (org.lwjgl.system MemoryUtil MemoryStack)
           (org.lwjgl BufferUtils))
  (:gen-class))

(def width 720)
(def height 720)
(def game-title "So Dope")

(defn setup
  "Initial game setup function. Returns the initial state."
  []
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
        program (opengl/link-shaders [(shaders/basic-vertex-shader)
                                      (shaders/basic-fragment-shader)])
        angle-uniform (opengl/get-uniform program "angle")]
    (GL20/glUseProgram program)
    (opengl/load-data vertices :array-buffer :float :static-draw)
    (opengl/load-data indices :element-array-buffer :byte :static-draw)
    (opengl/set-uniform angle-uniform :float angle)
    (opengl/link-attribute program "position" 2 :float false (* 5 Float/BYTES) 0)
    (opengl/link-attribute program "color" 3 :float false (* 5 Float/BYTES) (* 2 Float/BYTES))
    {:angle angle
     :angle-uniform angle-uniform}))

(defn update-state
  "Updates game state."
  [state]
  (assoc state :angle (mod (+ (:angle state) 1) 360)))

(defn render
  "Renders the game."
  [state]
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (opengl/set-uniform (:angle-uniform state) :float (:angle state))
  (GL11/glDrawElements GL11/GL_TRIANGLES 6 GL11/GL_UNSIGNED_BYTE 0))

(defn create-window [width height title]
  (GLFW/glfwDefaultWindowHints)
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (GLFW/glfwWindowHint
   GLFW/GLFW_OPENGL_PROFILE GLFW/GLFW_OPENGL_CORE_PROFILE)
  (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_FORWARD_COMPAT GL11/GL_TRUE)
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR 3)
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MINOR 2)
  (GLFW/glfwCreateWindow width height title MemoryUtil/NULL MemoryUtil/NULL))

(defn setup-glfw
  "Sets up GLFW options. Must have initiated GLFW and have a current OpenGL context."
  [window]
  (GLFW/glfwSwapInterval 1)
  (GLFW/glfwShowWindow window))

(defn setup-opengl
  "Sets up OpenGL. Must have a current OpenGL context."
  []
  (GL/createCapabilities)
  (GL11/glClearColor 0.0 0.0 0.0 0.0))

(defn terminate
  "Cleans up and terminates the program."
  []
  (GLFW/glfwTerminate))

(defn game-loop
  "The main game loop."
  [window state]
  (if (GLFW/glfwWindowShouldClose window)
    (terminate)
    (do (render state)
        (GLFW/glfwSwapBuffers window)
        (GLFW/glfwPollEvents)
        (game-loop window (update-state state)))))

(defn -main [& args]
  (when-not (GLFW/glfwInit)
    (throw (IllegalStateException. "Unable to initialize GLFW")))
  (GLFW/glfwSetErrorCallback (GLFWErrorCallback/createPrint System/err))
  (let [window (create-window width height game-title)]
    (when (= window nil)
      (throw (RuntimeException. "Error creating window")))
    (GLFW/glfwMakeContextCurrent window)
    (setup-opengl)
    (setup-glfw window)
    (let [state (setup)]
      (game-loop window state))))
