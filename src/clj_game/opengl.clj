(ns clj-game.opengl
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as string])
  (:import (org.lwjgl.opengl GL11 GL15 GL20)
           (org.lwjgl BufferUtils)))

(defn keyword-to-gl-const
  "Transforms a keyword into the equivalent GL_ constant."
  [gl-version keyword]
  (let [package "org.lwjgl.opengl"
        class (string/upper-case gl-version)
        const (-> (name keyword)
                  (string/replace #"-" "_")
                  (string/upper-case))
        gl-const (symbol (str package "." class "/" "GL_" const))]
    (eval gl-const)))

(defn make-shader
  "Creates and compiles a new shader.

  `type` is one of :vertex-shader, :fragment-shader, :geometry-shader,
  :tess-control-shader, :tess-evaluation-shader."
  [type source]
  (fn []
    (let [type-int (keyword-to-gl-const "GL20" type)
          shader (GL20/glCreateShader type-int)]
      (GL20/glShaderSource shader source)
      (GL20/glCompileShader shader)
      (if (= (GL20/glGetShaderi shader GL20/GL_COMPILE_STATUS) 1)
        shader
        (throw (RuntimeException. (GL20/glGetShaderInfoLog shader)))))))

(defn link-shaders
  "Links shaders into a program."
  ;; TODO support writing to multiple buffers from the fragment shader
  ;; via glBindFragDataLocation
  [shaders]
  (let [program (GL20/glCreateProgram)]
    (doseq [shader shaders]
      (GL20/glAttachShader program shader))
    (GL20/glLinkProgram program)
    program))

(defn to-float-buffer
  "Creates a java.nio.FloatBuffer from a sequence."
  [seq]
  (let [arr (float-array seq)]
    (-> (BufferUtils/createFloatBuffer (count arr))
        (.put arr)
        (.flip))))

(defn to-byte-buffer
  "Creates a java.nio.ByteBuffer from a sequence."
  [seq]
  (let [arr (byte-array (map byte seq))]
    (-> (BufferUtils/createByteBuffer (count arr))
        (.put arr)
        (.flip))))



(defn gen-buffer
  "Initializes a VBO on the graphics card.

  `buffer-type` is one of :array-buffer, :element-array-buffer,
  :pixel-pack-buffer, :pixel-unpack-buffer, :transform-feedback-buffer,
  :uniform-buffer, :texture-buffer, :copy-read-buffer, :copy-write-buffer,
  :draw-indirect-buffer, :atomic-counter-buffer, :dispatch-indirect-buffer,
  :shader-storage-buffer, :parameter-buffer-arb"
  [buffer-type]
  (let [buffer-type-int (keyword-to-gl-const "GL15" buffer-type)
        buffer (GL15/glGenBuffers)]
    (GL15/glBindBuffer buffer-type-int buffer)
    buffer))

(defn load-data
  "Loads data into a VBO on the graphics card.

  `buffer-type` is one of :array-buffer, :element-array-buffer,
  :pixel-pack-buffer, :pixel-unpack-buffer, :transform-feedback-buffer,
  :uniform-buffer, :texture-buffer, :copy-read-buffer, :copy-write-buffer,
  :draw-indirect-buffer, :atomic-counter-buffer, :dispatch-indirect-buffer,
  :shader-storage-buffer, :parameter-buffer-arb

  `data-type` is one of :float, :byte.

  `usage-hint` is one of :stream-draw, :stream-read, :stream-copy,
  :static-draw, :static-read, :static-copy, :dynamic-draw,
  :dynamic-read, :dynamic-copy"
  [data buffer-type data-type usage-hint]
  (let [buffer-data (match data-type
                           :float (to-float-buffer data)
                           :byte (to-byte-buffer data))
        buffer-type-int (keyword-to-gl-const "GL15" buffer-type)
        usage-hint-int (keyword-to-gl-const "GL15" usage-hint)]
    (GL15/glBufferData buffer-type-int buffer-data usage-hint-int)))

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

(defn get-uniform
  "Gets a uniform pointer."
  [program uniform]
  (GL20/glGetUniformLocation program uniform))

(defn get-uniform-setter
  "Gets the appropriate function to set a uniform based on type and arg count.

  `data-type` is one of :int, :float, :double"
  [data-type arg-count]
  (let [base "glUniform"
        package (str "org.lwjgl.opengl."
                     (match data-type
                            :double "GL40"
                            _ "GL20"))
        type-char (match data-type
                         :int "i"
                         :float "f"
                         :double "d")
        func-name (symbol (str package "/" base arg-count type-char))]
    (fn [uniform & args]
      (eval
       `(~func-name ~uniform ~@args)))))

(defn set-uniform
  "Sets the value of a uniform.

  `data-type` is one of :int, :float, :double"
  [uniform data-type & values]
  (let [setter (get-uniform-setter data-type (count values))]
    (apply setter uniform values)))
