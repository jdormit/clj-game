(ns clj-game.core
  (:require [clj-game.shaders :as shaders])
  (:import (org.lwjgl.glfw GLFW GLFWErrorCallback)
           (org.lwjgl.opengl GL GL11 GL15 GL20 GL30)
           (org.lwjgl.system MemoryUtil MemoryStack)
           (org.lwjgl BufferUtils))
  (:gen-class))

(def width 1280)
(def height 720)
(def game-title "So Dope")

(defn to-float-buffer
  "Creates a java.nio.FloatBuffer from a sequence"
  [seq]
  (let [arr (float-array seq)]
    (-> (BufferUtils/createFloatBuffer (count arr))
        (.put arr)
        (.flip))))

(defn setup
  "Initial game setup function. Returns the initial state"
  []
  (let [vao (GL30/glGenVertexArrays)]
    (GL30/glBindVertexArray vao)
    (let [triangle-verts [0.0 0.5 1.0 0.0 0.0
                          0.5 -0.5 0.0 1.0 0.0
                          -0.5 -0.5 0.0 0.0 1.0]
          vbo (GL15/glGenBuffers)
          program (GL20/glCreateProgram)]
      (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo)
      (GL15/glBufferData GL15/GL_ARRAY_BUFFER
                         (to-float-buffer (float-array triangle-verts))
                         GL15/GL_STATIC_DRAW)
      (GL20/glAttachShader program (shaders/basic-vertex-shader))
      (GL20/glAttachShader program (shaders/basic-fragment-shader))
      (GL30/glBindFragDataLocation program 0 "outColor")
      (GL20/glLinkProgram program)
      (GL20/glUseProgram program)
      (let [position-attribute (GL20/glGetAttribLocation program "position")
            color-attribute (GL20/glGetAttribLocation program "color")]
        (GL20/glVertexAttribPointer position-attribute
                                    2
                                    GL11/GL_FLOAT
                                    false
                                    (* 5 Float/BYTES)
                                    0)
        (GL20/glEnableVertexAttribArray position-attribute)
        (GL20/glVertexAttribPointer color-attribute
                                    3
                                    GL11/GL_FLOAT
                                    false
                                    (* 5 Float/BYTES)
                                    (* 2 Float/BYTES))
        (GL20/glEnableVertexAttribArray color-attribute)
        {}))))

(defn update-state
  "Updates game state"
  [state]
  state)

(defn render
  "Renders the game"
  [state]
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (GL11/glDrawArrays GL11/GL_TRIANGLES 0 3))

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
  "Sets up OpenGL. Must have a current OpenGL context"
  []
  (GL/createCapabilities)
  (GL11/glClearColor 0.0 0.0 0.0 0.0))

(defn terminate
  "Cleans up and terminates the program"
  []
  (GLFW/glfwTerminate))

(defn game-loop
  "The main game loop"
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
