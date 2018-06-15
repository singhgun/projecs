;lein test avScripts.avScripts_5

(ns avScripts.avScripts_5
    (use [clojure.java.shell :only [sh]])
    (use [utils.fileUtils :exclude [-main init]])
    (use utils.utils)
    (use clojure.test)
    (:gen-class)
    )

;(map (fn[[a1 a2 a3]] (println (format s a1 a2 a3))))

;(def ffmpeg "D:/mani/dev/opt/ffmpeg-20151208-git-ff6dd58-win64-static/bin/ffmpeg.exe")
(def ffmpeg (if (isLinux) 
    "ffmpeg" 
    "D:/mani/dev/opt/ffmpeg-20160614-git-cb46b78-win32-static/bin/ffmpeg.exe"))
;(def ffmpeg "ffmpeg")

(def handbrake "D:/mani/dev/opt/HandBrake-0.10.5-x86_64-Win_CLI/HandBrakeCLI.exe")

;(:1 ["command to check the title of DVD check the titles with longest duration -- sh handbrake -i file or <VIDEO_TS folder> --title 0" (str handbrake " -i %s --title 0"))

(defn loopEncode [f fileMap crf]
      (def output (reduce-kv (fn [res k v] (conj res (f k v crf))) [] fileMap))
      output)

(defn ffmpegEncodeX265
      ([inFile outFile crf]
        (def cmds {
                   :1 ["Encodes all videos only and copying all other streams as is"
                       (str ffmpeg " -i %s -map 0 -c copy -c:v libx265 -preset medium -x265-params crf=%s %s")]
                   :2 ["Encodes all videos/audios and copy all other streams as is"
                       (str ffmpeg " -i %s -map 0 -c copy -c:v libx265 -preset medium -x265-params crf=%s -c:a aac -strict experimental -b:a 96k %s")]
                   :3 ["Encodes videos only and copying all other primary streams(only)"
                       (str ffmpeg " -i %s -c copy -c:v libx265 -preset medium -x265-params crf=%s %s")]
                   :4 ["Encodes most significant audio and video only ignoring other streams"
                       (str ffmpeg " -i %s -c:v libx265 -preset medium -x265-params crf=%s -c:a aac -strict experimental -b:a 96k %s")]
                   :5 ["Input has 4 streams 0=v, 1=s, 2=jap audio, 3=eng audio -> Output has 3 streams mapped like 0=0 encoded video 0=1 subtitle copied 0=3 eng encoded audio"
                       (str ffmpeg " -i %s -map 0:0 -map 0:1 -map 0:3 -c:v libx265 -preset medium -x265-params crf=%s -c:a:3 aac -strict experimental -b:a:3 96k -c:s copy %s")]
                   })
        (let [x (second (cmds :2))]
             (format x inFile crf outFile)))
      ([dirPath]
        (println "Provide CRF : [0-23-51 least]")
        (def crf (read-line))
        (def f_fileMap (fileMap dirPath "" (str "_f_x265_" crf ".mkv")))
        (loopEncode ffmpegEncodeX265 f_fileMap crf))
      ([]
        (println "Provide input file : ")
        (def inFile (clojure.string/replace (read-line) #"\\" "/"))
        (println "Provide CRF : [0-23-51 least]")
        (def crf (read-line))
        (def outFile (str (.substring inFile 0 (.lastIndexOf inFile ".")) "_f_x265_" crf ".mkv"))
        (ffmpegEncodeX265 inFile outFile crf))
      )

(defn ffmpegVolume
      ([inFile outFile db]
        (def cmds {
                   :1 ["inc-dec volume of audio"
                       (str ffmpeg " -i %s -map 0 -c copy -c:a aac -af \"volume=%sdB\" %s")]
                   :2 ["inc-dec volume of audio"
                       (str ffmpeg " -i %s -vcodec copy -af \"volume=%sdB\" %s")]
                   })
        (let [x (second (cmds :1))]
             (format x inFile db outFile)))
      ([dirPath]
        (println "Provide db : ")
        (def db (read-line))
        (def f_fileMap (fileMap dirPath "" (str "_f_v_" db)))
        (loopEncode ffmpegVolume f_fileMap db))
      ([]
        (println "Provide input file : ")
        (def inFile (clojure.string/replace (read-line) #"\\" "/"))
        (println "Provide db : ")
        (def db (read-line))
        (def outFile (str (.substring inFile 0 (.lastIndexOf inFile ".")) "_f_v_" db (.substring inFile (.lastIndexOf inFile "."))))
        (println "outFukle dsjfasdjf" outFile)
        (ffmpegVolume inFile outFile db))
      )

(defn ffmpegEncodeForHDReadyTV
      ([inFile outFile crf]
        (def cmds {
                   :1 ["Change the fps to 24 of 1080 video"
                       (str ffmpeg " -i %s -r 24 -map 0 -c copy -c:v libx264 -preset slow -crf %s %s")]
                   :2 ["Change progressive to interlace"
                       (str ffmpeg " -i %s -map 0 -c copy -c:v libx264 -preset slow -crf %s -flags +ilme+ildct %s")]
                   :3 ["Change to 720p"
                       (str ffmpeg " -i %s -s hd720 -map 0 -c copy -c:v libx264 -crf %s -c:a aac %s")]
                   })
        (let [x (second (cmds :3))]
             (format x inFile crf outFile)))
      ([dirPath]
        (println "Provide CRF : [0-28-51 least]")
        (def crf (read-line))
        (def f_fileMap (fileMap dirPath "" (str "_f_x264_hdReadyTV_" crf ".mkv")))
        (loopEncode ffmpegEncodeForHDReadyTV f_fileMap crf))
      ([]
        (println "Provide input file : ")
        (def inFile (clojure.string/replace (read-line) #"\\" "/"))
        (println "Provide CRF : [0-28-51 least]")
        (def crf (read-line))
        (def outFile (str (.substring inFile 0 (.lastIndexOf inFile ".")) "_f_x264_hdReadyTV_" crf ".mkv"))
        (ffmpegEncodeForHDReadyTV inFile outFile crf))
      )

(defn ffmpegCut
    ([inFile outFile cutFrom cutTill]
        (def cmds {
                   :1 ["fast and copies only"
                       (str ffmpeg " -ss %s -i %s -ss 00:00:01 -t %s -c copy %s")]
                   :2 ["slow and transocde by syncing"
                       (str ffmpeg " -i %s -ss 00:01:00 -t 00:01:00 -async 1 -strict -2 %s")]
                   })

        (def cutDuration 0)
        (let [d1 (.parse (java.text.SimpleDateFormat. "HH:mm:ss") cutFrom)
             d2 (.parse (java.text.SimpleDateFormat. "HH:mm:ss") cutTill)
             duration (- (.getTime d2) (.getTime d1) (* 60 60 1000))]
             (def cutDuration (.format (java.text.SimpleDateFormat. "HH:mm:ss") duration)));60 hrs is also subtracted)

        (let [x (second (cmds :1))]
             (format x cutFrom inFile cutDuration outFile)))
      ([inFile outFile]
        (println "Provide cut from : [00:00:00]")
        (def cutFrom (read-line))
        (println "Provide cut till : [00:00:00]")
        (def cutTill (read-line))

        (ffmpegCut inFile outFile cutFrom cutTill))
      ([]
        (println "Provide input file : ")
        (def inFile (clojure.string/replace (read-line) #"\\" "/"))
        (def outFile (str (.substring inFile 0 (.lastIndexOf inFile ".")) "_cut" (.substring inFile (.lastIndexOf inFile "."))))
        (ffmpegCut inFile outFile))
      )

(defn ffmpegImport
      ([inFile srtFile outFile]
        (def cmds {
                   :1 ["Import srt file in video as mkv"
                       (str ffmpeg " -i %s -sub_charenc UTF-8 -i %s -map 0:v -map 0:a -c copy -map 1 -c:s:0 srt -metadata:s:s:0 language=en %s")]
                   :2 ["Import multiple subtitles"
                       (str ffmpeg " -i %s -sub_charenc UTF-8 -i %s -i %s -map 0:v -map 0:a -c copy -map 1 -c:s:0 srt -metadata:s:s:0 language=en -map 2 -c:s:1 srt -metadata:s:s:1 language=swe %s")]
                   :3 ["Import subtitles from mkv and hardcode"
                       (str ffmpeg " -i %s -c:a aac -vf subtitles=%s %s")]
                   })

        (def option (if (= inFile srtFile) :3 :1))
        (let [x (second (cmds option))]
             (prn x inFile srtFile outFile)
             (format x inFile srtFile outFile)))
      ([]
        (println "Provide input file : ")
        (def inFile (clojure.string/replace (read-line) #"\\" "/"))
        (def outFile (str (.substring inFile 0 (.lastIndexOf inFile ".")) "_en.mkv"))

        (println "Provide srt file or leave blank if same as input file: ")

        (def srtFile (clojure.string/replace (read-line) #"\\" "/"))

        (if (clojure.string/blank? srtFile)
          (ffmpegImport inFile inFile outFile)
          (ffmpegImport inFile srtFile outFile)))
      )

(defn handbrakeEncodeX265
      ([inFile outFile crf]
        (def cmds {
                   :1 ["Genral purpose command to encode videos with all subtitles"
                       (str handbrake " -i %s -o %s -e x265 -q %s -a 2 -s 1,2,3 -E av_aac --custom-anamorphic --keep-display-aspect -O")]
                   :2 ["Ripping the dvd, hacker's way sh handbrake -i <folder> -o <output-file> -v -e x265 -q 33 -a 2 -s 1,2,3 -E av_aac --custom-anamorphic --keep-display-aspect -O --title <1 or 2 the biggest duration> --audio 1"
                       (str handbrake " -i %s -o %s -v -e x265 -q %s -a 2 -s 1,2,3 -E av_aac --custom-anamorphic --keep-display-aspect -O --title 1 --audio 1")]
                   })
        (let [x (second (cmds :2))]
             (format x inFile outFile crf)))
      ([dirPath]
        (println "Provide CRF : ")
        (def crf (read-line))
        (def f_fileMap (fileMap dirPath "" (str "_h_x265_" crf ".mkv")))
        (loopEncode ffmpegEncodeX265 f_fileMap crf))
      ([]
        (println "Provide input file : ")
        (def inFile (clojure.string/replace (read-line) #"\\" "/"))
        (println "Provide CRF : ")
        (def crf (read-line))
        (def outFile (str (.substring inFile 0 (.lastIndexOf inFile ".")) "_h_x265_" crf ".mkv"))
        (handbrakeEncodeX265 inFile outFile crf))
      )

(defn ffmpegConcat []
      (println "Provide input file : ")
      (def inFile (clojure.string/replace (read-line) #"\\" "/"))
      (println "Provide output file : ")
      (def outFile (read-line))
      (def cmds {
                 :1 ["Concat all files provided in -f"
                     (str ffmpeg " -f concat -i %s -c copy %s")]
                 })
      (let [x (second (cmds :1))]
           (format x inFile outFile)))

(defn init []

      (def dirPath (if (isLinux) 
          "/home/manish/mani/video/compressed/"
          "D:/mani/video/compressed/"))

      (println "Welcome to AV Convertor\n Please provide source directory for videos - must end with / : ")
      (def dirPath #_(read-line) dirPath)

      (def cmdMap [[(str "ffmpeg -> x265 convert all in " dirPath) #(ffmpegEncodeX265 dirPath)]
                   [(str "ffmpeg -> x265 convert ") #(ffmpegEncodeX265)]
                   [(str "ffmpeg -> inc-dec volume of audio all in " dirPath) #(ffmpegVolume dirPath)]
                   [(str "ffmpeg -> inc-dec volume of audio ") #(ffmpegVolume)]
                   [(str "ffmpeg -> convert to HD ready TV ") #(ffmpegEncodeForHDReadyTV)]
                   [(str "ffmpeg -> convert to HD ready TV in " dirPath) #(ffmpegEncodeForHDReadyTV dirPath)]
                   [(str "ffmpeg -> cut ") #(ffmpegCut)]
                   [(str "ffmpeg -> import subtitle ") #(ffmpegImport)]
                   [(str "ffmpeg -> concat videos in file ") #(ffmpegConcat)]
                   [(str "handbrake -> x265 convert all in " dirPath) #(handbrakeEncodeX265 dirPath)]
                   [(str "handbrake -> x265 convert ") #(handbrakeEncodeX265)]
                   ])

      (def i 0)
      (doall (map (fn [[v1 v2]] (println i "." v1) (def i (inc i))) cmdMap))

      #_(def cmdMap (array-map :1 [(str "ffmpeg -> x265 convert all in " dirPath) #(ffmpegEncodeX265 (fileMap dirPath fromExt "_f_x265_27.mkv") "27")] :3 [(str "ffmpeg -> x264 convert all in " dirPath) #(ffmpegEncodeX264 (fileMap dirPath fromExt "_f_x264_21.mkv") "21")] :4 [(str "ffmpeg -> x264 convert " srcFile " -> " outFile) #(ffmpegEncodeX264 srcFile outFile "21")] :5 [(str "ffmpeg -> cut " srcFile " -> " outFile) #(ffmpegCut srcFile outFile)] :6 [(str "ffmpeg -> import subtitle " srcFile " + " srtFile " -> " outFile) #(ffmpegImport srcFile srtFile outFile)] :7 [(str "ffmpeg -> concat videos in file " concatFilesIn " -> " outFile) #(ffmpegConcat concatFilesIn outFile)] :8 [(str "ffmpeg -> inc-dec volume of audio all in " dirPath) #(ffmpegVolume (fileMap dirPath fromExt "_v9") 9)] :9 [(str "ffmpeg -> inc-dec volume of audio " srcFile " -> " outFile) #(ffmpegVolume srcFile outFile 19)] :10 [(str "ffmpeg -> convert to HD ready TV " srcFile " -> " outFile) #(ffmpegEncodeForHDReadyTV srcFile outFile 19)] :11 [(str "ffmpeg -> convert to HD ready TV in " dirPath) #(ffmpegEncodeForHDReadyTV (fileMap dirPath fromExt "_f_x264_hdReady.mkv") "19")] :12 [(str "ffmpeg -> mpeg2 convert all in " dirPath) #(ffmpegEncodeMpeg2 (fileMap dirPath fromExt "_f_mpeg2_5.mpg") "5")] :13 [(str "handbrake -> x265 convert all in " dirPath) #(handbrakeEncodeX265 (fileMap dirPath fromExt "_h_x265_21.mkv") "21")] :14 [(str "handbrake -> x265 convert " srcFile " -> " outFile) #(handbrakeEncodeX265 srcFile outFile "21")]))
      #_(doseq [keyval cmdMap] (println (name (key keyval)) "-" ((val keyval) 0)))

      (println "Chose Option!")
      (def option (read-line) #_(keyword (read-line)))

      (def output (time (((cmdMap (Integer/parseInt option)) 1)) #_(((cmdMap option) 1))))

      (def output (if (not (vector? output)) (conj [] output) output))

      (doall (map #(println "exec : " %) output))
      ;(doseq [out output] (println "exec all: " out))

      (println "Should I start executing them! y/n")
      (def option (read-line))

      (def execOutput [])
      (if (= option "y")
        (doseq [out output]
               (println "exec: " out)
               (def execOutput (concat execOutput (executeSH (spliter out)))))
        (def execOutput "Chose n/N!"))

      (println execOutput)

      #_(if (vector? execOutput)
          (doseq [out execOutput]
                 (clojure.string/blank? (out :err))
                 (println (out :err)))
          (println (execOutput :err)))

      ;extract video and specific tracks from the video e.g. 0:0 video 0:1 audio 0:2 audio 0:3 srt
      ;(executeSH (spliter (format (str ffmpeg " -i %s -map 0:0 -map 0:2 -c copy %s") srcFile outFile)))))

      (System/exit 0)
      )

(deftest testAvScripts
    (init))

(defn -main [args] (init))