(ns blackjack.game
  (:require [card-ascii-art.core :as card-core]))

(defn new-card []
  "Generate a card number between 1 and 13."
  (inc (rand-int 13)))

(defn JQK->10 [card]
  "Convert face cards to 10 points."
  (if (> card 10) 10 card))

(defn A->11 [card]
  "Convert Ace to 11 points."
  (if (= card 1) 11 card))

(defn points-cards [cards]
  "Calculate the total points of the cards."
  (let [cards-without-JQK (map JQK->10 cards)
        cards-with-A11 (map A->11 cards-without-JQK)
        points-with-A-1 (reduce + cards-without-JQK)
        points-with-A-11 (reduce + cards-with-A11)]
    (if (> points-with-A-11 21) points-with-A-1 points-with-A-11)))

(defn player [player-name]
  (let [card1 (new-card)
        card2 (new-card)
        cards [card1 card2]
        points (points-cards cards)]
    {:player-name player-name
     :cards cards
     :points points}))

(defn more-card [player]
  (let [card (new-card)
        cards (conj (:cards player) card)
        new-player (update player :cards conj card)
        points (points-cards cards)]
    (assoc new-player :points points)))

(defn player-decision-continue? [player]
  (= (read-line) "sim"))

(defn dealer-decision-continue? [player-points dealer]
  (let [dealer-points (:points dealer)]
    (< dealer-points player-points)))

(defn game [player fn-decision-continue?]
  (println (:player-name player) ": mais carta?")
  (if (fn-decision-continue? player)
    (let [player-with-more-cards (more-card player)]
      (card-core/print-player player-with-more-cards)
      (recur player-with-more-cards fn-decision-continue?))
      player))

(def player01 (player "Victor Nevola"))
(card-core/print-player player01)

(def dealer (player "Dealer"))
(card-core/print-player dealer)

(def player-after-game (game player01 player-decision-continue?))
(game dealer (partial dealer-decision-continue? (:points player-after-game)))