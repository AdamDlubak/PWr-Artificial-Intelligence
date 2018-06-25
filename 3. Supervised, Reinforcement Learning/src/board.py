import copy
import numpy as np
from src.game_evaluator import GameEvaluator



class Board:
    def __init__(self, grid_shape=(3, 3), evaluator=GameEvaluator()):
        self.evaluator = evaluator
        self.grid = np.ones(grid_shape) * np.nan

    def reset_board(self):
        self.grid = np.ones((3, 3)) * np.nan

    def over(self):  # The game is over if there is a winner or if no squares remain empty (cat's game)
        return (not np.any(np.isnan(self.grid))) or (self.winner() is not None)

    def get_available_moves(self):
        return [(i, j) for i in range(3) for j in range(3) if np.isnan(self.grid[i][j])]

    def place_mark(self, move, mark):
        numerical_mark = self.mark_to_num(mark)
        self.grid[tuple(move)] = numerical_mark

    def mark_to_num(self, mark):
        d = {"X": 1, "O": 0}
        return d[mark]

    def get_board_after_move(self, move, mark):
        board = copy.deepcopy(self)
        board.place_mark(move, mark)
        return board

    def winner(self):
        rows = [self.grid[i, :] for i in range(3)]
        cols = [self.grid[:, j] for j in range(3)]
        diag = [np.array([self.grid[i, i] for i in range(3)])]
        cross_diag = [np.array([self.grid[2 - i, i] for i in range(3)])]
        lanes = np.concatenate(
            (rows, cols, diag, cross_diag))  # A "lane" is defined as a row, column, diagonal, or cross-diagonal

        any_lane = lambda x: any(
            [np.array_equal(lane, x) for lane in lanes])  # Returns true if any lane is equal to the input argument "x"
        if any_lane(np.ones(3)):
            return "X"
        elif any_lane(np.zeros(3)):
            return "O"

    def make_key(self, mark):
        fill_value = 9
        filled_grid = copy.deepcopy(self.grid)
        np.place(filled_grid, np.isnan(filled_grid), fill_value)
        return "".join(map(str, (list(map(int, filled_grid.flatten()))))) + mark

    def give_reward(self):  # Assign a reward for the player with mark X in the current board position.
        if self.evaluator.is_game_over(self.grid):
            winner = self.evaluator.is_winner(self.grid)
            if winner is not None:
                if winner == "X":
                    return 1.0  # X won
                elif winner == "O":
                    return -1.0  # O won
            else:
                return 0.5  # draw
        else:
            return 0.0  # game not finished yet

    def grid_to_vec(self, grid):
        model_vec = grid.flatten()
        model_vec[model_vec == 0] = -1
        model_vec = np.nan_to_num(model_vec)
        return model_vec


