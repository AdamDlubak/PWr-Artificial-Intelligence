import numpy as np

from src.game_evaluator import GameEvaluator
from src.players.computer_player import ComputerPlayer
from src.players.random_player import RandomPlayer


class QPlayer(ComputerPlayer):
    def __init__(self, mark, Q={}, epsilon=0.0):
        super(QPlayer, self).__init__(mark=mark)
        self.Q = Q
        self.epsilon = epsilon

    def get_move(self, board):
        if np.random.uniform() < self.epsilon:  # With probability epsilon, choose a move at random ("epsilon-greedy" exploration)
            return RandomPlayer.get_move(board)
        else:
            state_key = QPlayer.make_and_maybe_add_key(board, self.mark, self.Q)
            Qs = self.Q[state_key]

            if self.mark == "X":
                return QPlayer.stochastic_argminmax(Qs, max)
            elif self.mark == "O":
                return QPlayer.stochastic_argminmax(Qs, min)


    @staticmethod
    def make_and_maybe_add_key(board, mark,
                               Q):  # Make a dictionary key for the current state (board + player turn) and if Q does not yet have it, add it to Q
        default_Qvalue = 1.0  # Encourages exploration
        state_key = board.make_key(mark)
        if Q.get(state_key) is None:
            evaluator = GameEvaluator()
            moves = evaluator.get_available_moves(board.grid)
            Q[state_key] = {move: default_Qvalue for move in
                            moves}  # The available moves in each state are initially given a default value of zero
        return state_key

    @staticmethod
    def stochastic_argminmax(Qs,
                             min_or_max):  # Determines either the argmin or argmax of the array Qs such that if there are 'ties', one is chosen at random
        min_or_max_q = min_or_max(list(Qs.values()))
        if list(Qs.values()).count(
                min_or_max_q) > 1:  # If there is more than one move corresponding to the maximum Q-value, choose one at random
            best_options = [move for move in list(Qs.keys()) if Qs[move] == min_or_max_q]
            move = best_options[np.random.choice(len(best_options))]
        else:
            move = min_or_max(Qs, key=Qs.get)
        return move