(ns clj-game.core
  (:require [clj-game.opengl :as opengl])
  (:import (org.lwjgl.glfw GLFW GLFWErrorCallback)
           (org.lwjgl.opengl GL GL11)
           (org.lwjgl.system MemoryUtil))
  (:gen-class))

(def width 720)
(def height 720)
(def game-title "So Dope")

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
  [window state update-state render]
  (if (GLFW/glfwWindowShouldClose window)
    (terminate)
    (do (render state)
        (GLFW/glfwSwapBuffers window)
        (GLFW/glfwPollEvents)
        (game-loop window (update-state state) update-state render))))

(defn -main [& args]
  (require (symbol (first args)))
  (let [ns-arg (first args)
        setup (eval (symbol (str ns-arg "/setup")))
        update-state (eval (symbol (str ns-arg "/update-state")))
        render (eval (symbol (str ns-arg "/render")))]
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
        (game-loop window state update-state render)))))
