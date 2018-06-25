from src.board import Board
from src.players.computer_player import ComputerPlayer
from src.players.human_player import HumanPlayer
from src.players.supervised_player import SupervisedPlayer
from src.players.q_player import QPlayer
from src.players.random_player import RandomPlayer
from src.q_learning import QLearning
from src.user_interface import UserInterface
from src.game_evaluator import GameEvaluator
from src import supervised_learning as sl
import pickle as pickle
import os
import numpy as np


class Game:

    def __init__(self, window, player1, player2, data_dir_path, Q_learn=None, Q={}, alpha=0.3, gamma=0.9):

        self.data_dir_path = data_dir_path
        self.data_file_path = data_dir_path + "/"

        self.window = window
        self.user_interface = UserInterface(window, self)
        self.board = Board()
        self.evaluator = GameEvaluator()
        self.is_q_learn_mode = False

        self.player1 = player1
        self.player2 = player2
        self.current_player = player1
        self.other_player = player2

        self.games, self.game_stats, self.game_story = self.prepare_stats()
        self.Q_learn = QLearning(self.player1, self.player2)

    def prepare_stats(self):
        games = 0
        game_stats = []
        game_story = []

        for i in range(2):
            game_stats.append([])
            game_story.append([])
            for j in range(3):
                game_stats[i].append(0)
                game_story[i].append([])

        for i in range(2):
            for j in range(3):
                game_story[i][j].append(0)

        return games, game_stats, game_story

    def callback(self, button):
        if self.evaluator.is_game_over(self.board.grid):
            pass  # Do nothing if the game is already over
        else:
            if isinstance(self.current_player, HumanPlayer) and isinstance(self.other_player, HumanPlayer):
                if self.user_interface.empty(button):
                    move = self.get_move(button)
                    self.handle_move(move, self.board, self.current_player, self.other_player, self.evaluator)
            elif isinstance(self.current_player, HumanPlayer) and isinstance(self.other_player, ComputerPlayer):
                computer_player = self.other_player
                if self.user_interface.empty(button):
                    human_move = self.get_move(button)
                    self.handle_move(human_move, self.board, self.current_player, self.other_player, self.evaluator)
                    if not self.evaluator.is_game_over(self.board.grid):  # Trigger the computer's next move
                        computer_move = computer_player.get_move(self.board)
                        self.handle_move(computer_move, self.board, self.current_player, self.other_player,
                                         self.evaluator)

    def get_move(self, button):
        info = button.grid_info()
        move = (int(info["row"]), int(info["column"]))  # Get move coordinates from the button's metadata
        return move

    def make_move(self, move):
        i, j = move  # Get row and column number of the corresponding button
        self.user_interface.set_button_mark(i, j, self.current_player.mark)  # Change the label on the button to the current player's mark
        self.board.place_mark(move, self.current_player.mark)  # Update the board

    def handle_move(self, move, board, current_player, other_player, evaluator):
        if isinstance(self.current_player, QPlayer):
            if self.Q_learn is not None:
                self.Q_learn.learn_Q(move, board, current_player, other_player, evaluator)

        self.make_move(move)

        if self.evaluator.is_game_over(self.board.grid):
            return self.declare_outcome()
        else:
            self.switch_players()

    def update_stats(self, win):
        self.games += 1
        if win == 1:
            self.game_stats[0][0] += 1
            self.game_stats[1][2] += 1
        elif win == 0:
            self.game_stats[0][1] += 1
            self.game_stats[1][1] += 1
        else:
            self.game_stats[0][2] += 1
            self.game_stats[1][0] += 1

        for i in range(2):
            for j in range(3):
                self.game_story[i][j].append(self.game_stats[i][j])

    def declare_outcome(self):
        self.user_interface.disable_board()
        if self.evaluator.is_winner(self.board.grid) is None:
            self.user_interface.set_label_text("The game is over. The game ended in a draw!")
            result = 1
        else:
            self.user_interface.set_label_text("The game is over. The player with mark {mark} won!".format(mark=self.current_player.mark))
            if self.current_player == self.player1:
                result = 0
            else:
                result = 2
        self.update_stats(result)

        if self.user_interface.live_preview_enable | isinstance(self.player1, HumanPlayer) | isinstance(self.player2, HumanPlayer):
            self.user_interface.update_stats()

        return result

    def switch_players(self):
        if self.current_player == self.player1:
            self.current_player = self.player2
            self.other_player = self.player1
        else:
            self.current_player = self.player1
            self.other_player = self.player2

    def play(self):
        if isinstance(self.player1, HumanPlayer) and isinstance(self.player2, HumanPlayer):
            pass  # For human vs. human, play relies on the callback from button presses
        elif isinstance(self.player1, HumanPlayer) and isinstance(self.player2, ComputerPlayer):
            pass
        elif isinstance(self.player1, ComputerPlayer) and isinstance(self.player2, HumanPlayer):
            first_computer_move = self.player1.get_move(
                self.board)  # If player 1 is a computer, it needs to be triggered to make the first move.
            self.handle_move(first_computer_move, self.board, self.current_player, self.other_player, self.evaluator)
        elif isinstance(self.player1, ComputerPlayer) and isinstance(self.player2, ComputerPlayer):
            game_story = []
            result = 0
            while not self.board.over():  # Make the two computer players play against each other without button presses
                result = self.play_turn()
                game_story.append(self.board.grid_to_vec(self.board.grid))
            result = (result / 2)
            for i in range(len(game_story)):
                game_story[i] = np.append(game_story[i], result)
            return game_story

    def play_turn(self):
        move = self.current_player.get_move(self.board)
        return self.handle_move(move, self.board, self.current_player, self.other_player, self.evaluator)

    def change_player(self, new_player, player_number):
        self.is_q_learn_mode = False
        if player_number == 1:
            mark = "X"
        else:
            mark = "O"

        if new_player == "Random Player":
            player = RandomPlayer(mark=mark)
        elif new_player == "Q Player":
            player = QPlayer(mark)
        elif new_player == "Supervised Player":
            player = SupervisedPlayer(mark=mark)
        elif new_player == "Q Player in Learning Mode":
            self.is_q_learn_mode = True
            player = QPlayer(mark)
        else:
            player = HumanPlayer(mark=mark)

        if player_number == 1:
            self.player1 = player
        else:
            self.player2 = player

    def get_file_from_player(self, player):
        if player == 1:
            file = self.user_interface.learn_file_1
        else:
            file = self.user_interface.learn_file_2
        return file

    def get_Q(self, player):
        file = self.get_file_from_player(player)
        try:
            Q = pickle.load(open(self.data_file_path + file, "rb"))
        except pickle.UnpicklingError:
            self.user_interface.set_label_text("Wrong file, choose another!")
            Q = []
        return Q

    def new_game(self):

        if self.user_interface.used_player_1 == "Q Player":
            self.player1.Q = self.get_Q(1)
        if self.user_interface.used_player_2 == "Q Player":
            self.player2.Q = self.get_Q(2)

        if self.user_interface.used_player_1 == "Q Player in Learning Mode":
            self.epsilon = self.user_interface.get_epsilon()
            self.player1.epsilon = self.epsilon
        if self.user_interface.used_player_2 == "Q Player in Learning Mode":
            self.epsilon = self.user_interface.get_epsilon()
            self.player2.epsilon = self.epsilon

        if isinstance(self.player1, SupervisedPlayer):
            file = self.get_file_from_player(1)
            self.player1.model = sl.prepare_model(self.data_file_path,  file)
        if isinstance(self.player2, SupervisedPlayer):
            file = self.get_file_from_player(2)
            self.player2.model = sl.prepare_model(self.data_file_path, file)

        self.user_interface.status_label.config(text="Yeah, May the best man win!")


        if isinstance(self.player1, ComputerPlayer) & isinstance(self.player2, ComputerPlayer):
            iterations = self.user_interface.get_iterations()

            game_story = []

            for i in range(iterations):
                game_story += self.reset()
            if not self.user_interface.live_preview_enable:
                self.user_interface.update_stats()

            self.q_learn_mode_save_to_file(iterations, game_story)

        else:
            self.reset()

    def q_learn_mode_save_to_file(self, iterations, game_story):
        if self.is_q_learn_mode:
            file = self.data_file_path + "Q_Learning_{}_{}.p".format(self.epsilon, iterations)
            q_learn_file = self.open_file(file)
            Q = self.Q_learn.Q
            pickle.dump(Q, q_learn_file)
            q_learn_file.close()


            file = self.data_file_path + "SuperVised_{}_{}.csv".format(self.epsilon, iterations)
            np.savetxt(file, game_story, delimiter=",")

    def open_file(self, file):
        if os.path.isfile(file):
            os.remove(file)

        return open(file, 'wb')
    def reset(self):
        self.board.reset_board()
        self.user_interface.reset_board(self)
        self.current_player = self.player1
        self.other_player = self.player2
        return self.play()

    def exit_game(self):
        exit(0)
