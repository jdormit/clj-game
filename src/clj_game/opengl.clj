(ns clj-game.opengl
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as string])
  (:import (org.lwjgl.opengl GL11 GL15 GL20)
           (org.lwjgl BufferUtils)))

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

(defn link-shaders
  "Links shaders into a program"
  ;; TODO support writing to multiple buffers from the fragment shader
  ;; via glBindFragDataLocation
  [shaders]
  (let [program (GL20/glCreateProgram)]
    (doseq [shader shaders]
      (GL20/glAttachShader program shader))
    (GL20/glLinkProgram program)
    program))

(defn to-float-buffer
  "Creates a java.nio.FloatBuffer from a sequence"
  [seq]
  (let [arr (float-array seq)]
    (-> (BufferUtils/createFloatBuffer (count arr))
        (.put arr)
        (.flip))))

(defn to-byte-buffer
  "Creates a java.nio.ByteBuffer from a sequence"
  [seq]
  (let [arr (byte-array (map byte seq))]
    (-> (BufferUtils/createByteBuffer (count arr))
        (.put arr)
        (.flip))))

(defn keyword-to-gl-const
  "Transforms a keyword into the equivalent GL_ constant"
  [gl-version keyword]
  (eval (read-string
         (str "org.lwjgl.opengl."
              (string/upper-case gl-version)
              "/"
              "GL_"
              (-> (name keyword)
                  (string/replace #"-" "_")
                  (string/upper-case))))))

(defn load-data
  "Loads data into a VBO on the graphics card.

  `data-type` is one of :float, :byte.

  `buffer-type` is one of :array-buffer, :element-array-buffer,
  :pixel-pack-buffer, :pixel-unpack-buffer, :transform-feedback-buffer,
  :uniform-buffer, :texture-buffer, :copy-read-buffer, :copy-write-buffer,
  :draw-indirect-buffer, :atomic-counter-buffer, :dispatch-indirect-buffer,
  :shader-storage-buffer, :parameter-buffer-arb

  `usage-hint` is one of :stream-draw, :stream-read, :stream-copy,
  :static-draw, :static-read, :static-copy, :dynamic-draw,
  :dynamic-read, :dynamic-copy"
  [data data-type buffer-type usage-hint]
  (let [buffer-data (match data-type
                           :float (to-float-buffer data)
                           :byte (to-byte-buffer data))
        buffer-type-int (keyword-to-gl-const "GL15" buffer-type)
        usage-hint-int (keyword-to-gl-const "GL15" usage-hint)
        buffer (GL15/glGenBuffers)]
    (GL15/glBindBuffer buffer-type-int buffer)
    (GL15/glBufferData buffer-type-int buffer-data usage-hint-int)
    buffer))

(defn link-attribute
  "Links a shader attribute to values in a buffer on the graphics card.

  `data-type` is one of :byte, :unsigned-byte, :short, :unsigned-short,
  :int, :unsigned-int, :half-float, :float, :double, :fixed"
  [program attribute data-count data-type normalized step offset]
  (let [attrib-location (GL20/glGetAttribLocation program attribute)]
    (GL20/glVertexAttribPointer attrib-location
                                data-count
                                (keyword-to-gl-const "GL11" data-type)
                                normalized
                                step
                                offset)
    (GL20/glEnableVertexAttribArray attrib-location)
    attrib-location))
