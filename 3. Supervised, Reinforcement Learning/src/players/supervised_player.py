import numpy as np

from src.game_evaluator import GameEvaluator
from src.players.computer_player import ComputerPlayer


class SupervisedPlayer(ComputerPlayer):

    def __init__(self, mark, model = None):
        super(SupervisedPlayer, self).__init__(mark=mark)
        self.model = model

    def get_move(self, board):
        evaluator = GameEvaluator()
        moves = evaluator.get_available_moves(board.grid)
        future_grids = [board.get_board_after_move(move, self.mark).grid for move in moves]
        vectors = [board.grid_to_vec(grid) for grid in future_grids]
        results = self.model.predict(vectors)
        val = np.argmax(results, axis=0)
        return moves[val]