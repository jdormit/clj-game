(ns clj-game.core
  (:import (org.lwjgl.glfw GLFW)
           (org.lwjgl.opengl GL11)
           (org.lwjgl.system MemoryUtil))
  (:gen-class))

(def width 1280)
(def height 720)
(def game-title "Game")

(defn setup
  "Initial game setup setup. Returns the initial state"
  []
  ())

(defn update-state
  "Updates game state"
  [state]
  state)

(defn render
  "Renders the game"
  [state]
  ())

(defn create-window [width height title]
  (GLFW/glfwCreateWindow width height title MemoryUtil/NULL MemoryUtil/NULL))

(defn setup-glfw
  "Sets up GLFW options. Must have initiated GLFW and have a current OpenGL context."
  [window]
  (GLFW/glfwSwapInterval 1))

(defn cleanup
  "Cleans up and terminates the program"
  []
  (GLFW/glfwTerminate))

(defn game-loop
  "The main game loop"
  [window state]
  (if (GLFW/glfwWindowShouldClose window)
    (cleanup)
    (do (render state)
        (GLFW/glfwSwapBuffers window)
        (GLFW/glfwPollEvents)
        (game-loop window (update-state state)))))

(defn -main [& args]
  (GLFW/glfwInit)
  (let [state (setup)
        window (create-window width height game-title)]
    (GLFW/glfwMakeContextCurrent window)
    (setup-glfw window)
    (game-loop window state)))
