(ns matrix-animation.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reanimated.core :as anim]
            [reagent.ratom :as ratom]))

(enable-console-print!)

(println "This text is printed from src/matrix-animation/core.cljs. Go ahead and edit it and see reloading in action.")

(defn text-component [in]
  (let [ba (reagent/atom in)
        ya (ratom/reaction (:y @ba))
        y (anim/interpolate-to ya {:duration 1000})
        text (:text @ba)
        font-size (:size @ba)]
    (fn [{:keys [x] :as texts}]
      (reset! ba texts)
      [:g
       {:transform (str "translate(" x " " @y ")")}
       [:text {:transform "rotate(-90)"
               :text-length 300
               :fill "#1ec503"
               :filter "url(#blur)"
               :font-size font-size}
          text]])))

(defn one-text [{:keys [dy] :as text}]
  (update text :y (fn [y]
                      (-> (+ y dy)
                        (mod 3000)))))

(defn text-step [texts]
  (into texts
        (for [[k v] texts]
          [k (one-text v)])))

(defn new-text []
  {:text "(def (println \"Hello, world\"))"
   :dy (* (rand-nth [1 0.5 -0.5 -1]) (+ 25 (rand-int 15)))
   :size (+ 10 (rand-int 22))
   :x (+ 200 (rand-int 1000))
   :y (+ 200 (rand-int 1000))})

(defn react-to-value-example-component []
  (let [app-state (reagent/atom
                    {:texts (zipmap (repeatedly gensym)
                                    (repeatedly 100 new-text))})]
    (fn a-react-to-value-example-component []
      [:svg
        {:height "100%"
         :width "100%"
         :style {:background "black"}}
        [:filter {:id "blur"}
         [:feGaussianBlur {:std-deviation "1"}]]
       [anim/interval #(swap! app-state update :texts text-step) 1000]
       (for [[k v] (:texts @app-state)]
         ^{:key k}
         [text-component v])])))

(reagent/render-component [react-to-value-example-component]
                          (.getElementById js/document "app"))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
