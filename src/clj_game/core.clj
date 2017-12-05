(ns clj-game.core
  (:import (org.lwjgl.glfw GLFW)
           (org.lwjgl.opengl GL11)
           (org.lwjgl.system MemoryUtil))
  (:gen-class))

(defn open-window []
  (let [window (GLFW/glfwCreateWindow 500 500 "Test" MemoryUtil/NULL MemoryUtil/NULL)]
    (GLFW/glfwShowWindow window)
    window))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (GLFW/glfwInit)
  (let [window (GLFW/glfwCreateWindow 640 480 "Test" MemoryUtil/NULL MemoryUtil/NULL)]
    (GLFW/glfwMakeContextCurrent window)
    (while (not (GLFW/glfwWindowShouldClose window))
      (GLFW/glfwSwapBuffers window)
      (GLFW/glfwPollEvents)))
  (GLFW/glfwTerminate))
