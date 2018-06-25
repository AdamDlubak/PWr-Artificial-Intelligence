import numpy as np

from src.players.computer_player import ComputerPlayer


class RandomPlayer(ComputerPlayer):
    @staticmethod
    def get_move(board):
        moves = board.get_available_moves()
        if moves:
            return moves[np.random.choice(len(moves))]