import numpy as np


class GameEvaluator:

    def is_winner(self, grid):
        rows = [grid[i, :] for i in range(3)]
        columns = [grid[:, j] for j in range(3)]
        diagonal = [np.array([grid[i, i] for i in range(3)])]
        cross_diagonal = [np.array([grid[2 - i, i] for i in range(3)])]
        lanes = np.concatenate((rows, columns, diagonal, cross_diagonal))

        any_lane = lambda x: any([np.array_equal(lane, x) for lane in lanes])
        if any_lane(np.ones(3)):
            return "X"
        elif any_lane(np.zeros(3)):
            return "O"

    def is_game_over(self, grid):
        return (not np.any(np.isnan(grid))) or (self.is_winner(grid) is not None)

    def get_available_moves(self, grid):
        return [(i, j) for i in range(3) for j in range(3) if np.isnan(grid[i][j])]
