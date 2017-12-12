(def os-classifier "natives-macos")

(defproject clj-game "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.lwjgl/lwjgl "3.1.5"]
                 [org.lwjgl/lwjgl-assimp "3.1.5"]
                 [org.lwjgl/lwjgl-bgfx "3.1.5"]
                 [org.lwjgl/lwjgl-glfw "3.1.5"]
                 [org.lwjgl/lwjgl-nanovg "3.1.5"]
                 [org.lwjgl/lwjgl-nuklear "3.1.5"]
                 [org.lwjgl/lwjgl-openal "3.1.5"]
                 [org.lwjgl/lwjgl-opengl "3.1.5"]
                 [org.lwjgl/lwjgl-par "3.1.5"]
                 [org.lwjgl/lwjgl-stb "3.1.5"]
                 [org.lwjgl/lwjgl-vulkan "3.1.5"]
                 ; LWJGL Natives
                 [org.lwjgl/lwjgl "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-assimp "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-bgfx "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-glfw "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-nanovg "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-nuklear "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-openal "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-opengl "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-par "3.1.5"
                  :classifier ~os-classifier]
                 [org.lwjgl/lwjgl-stb "3.1.5"
                  :classifier ~os-classifier]]
  :main ^:skip-aot clj-game.core
  :uberjar-name "game.jar"
  :jvm-opts ["-XstartOnFirstThread"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
