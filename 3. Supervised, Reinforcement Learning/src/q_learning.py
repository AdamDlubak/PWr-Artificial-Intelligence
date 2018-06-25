from src.players.q_player import QPlayer


class QLearning:
    def __init__(self, player1, player2, Q={}, alpha=0.3, gamma=0.9):
        self.Q = Q
        self.alpha = alpha  # Learning rate
        self.gamma = gamma  # Discount rate
        self.share_Q_with_players(player1, player2)

    def share_Q_with_players(self, player1, player2):  # The action value table Q is shared with the QPlayers to help them make their move decisions
        if isinstance(player1, QPlayer):
            player1.Q = self.Q
        if isinstance(player2, QPlayer):
            player2.Q = self.Q

    def learn_Q(self, move, board, current_player, other_player, evaluator):  # If Q-learning is toggled on, "learn_Q" should be called after receiving a move from an instance of Player and before implementing the move (using Board's "place_mark" method)
        global expected
        state_key = QPlayer.make_and_maybe_add_key(board, current_player.mark, self.Q)
        next_board = board.get_board_after_move(move, current_player.mark)
        reward = next_board.give_reward()
        next_state_key = QPlayer.make_and_maybe_add_key(next_board, other_player.mark, self.Q)
        if evaluator.is_game_over(next_board.grid):
            expected = reward
        else:
            next_Qs = self.Q[
                next_state_key]  # The Q values represent the expected future reward for player X for each available move in the next state (after the move has been made)
            if current_player.mark == "X":
                expected = reward + (self.gamma * min(
                    next_Qs.values()))  # If the current player is X, the next player is O, and the move with the minimum Q value should be chosen according to our "sign convention"
            elif current_player.mark == "O":
                expected = reward + (self.gamma * max(
                    next_Qs.values()))  # If the current player is O, the next player is X, and the move with the maximum Q value should be chosen
        change = self.alpha * (expected - self.Q[state_key][move])
        self.Q[state_key][move] += change