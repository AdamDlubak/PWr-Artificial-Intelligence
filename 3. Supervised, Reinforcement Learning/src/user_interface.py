from tkinter import *
import tkinter as tk
from src.players.human_player import HumanPlayer
from src.players.supervised_player import SupervisedPlayer
from src.players.q_player import QPlayer
from decimal import Decimal
from os import listdir
from os.path import isfile, join

import matplotlib
matplotlib.use("TkAgg")
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib import style
from matplotlib.figure import Figure
style.use("ggplot")

class UserInterface:
    def __init__(self, window, game):
        self.window = window
        self.live_preview_enable = True
        self.empty_text = ""
        self.game = game

        user_options = [
            "Human Player",
            "Random Player",
            "Q Player",
            "Supervised Player"
        ]
        user_options_with_learning = user_options + ["Q Player in Learning Mode"]


        window.title("Tic Tac Toe")
        frames = self.create_window_structure()
        self.set_default_values_for_controls(frames, user_options, user_options_with_learning)
        self.create_controls(user_options, user_options_with_learning)
        self.create_plots_for_stats(frames)

    def create_check(self, text, row, padding_top, command):
        check = tk.Checkbutton(self.button_frame, text=text, state=DISABLED, command=command)
        check.grid(row=row, column=0, sticky=tk.W + tk.E, pady=(padding_top, 0))
        return check

    def create_option_menu(self, row, padding_top, default_value, list, command):
        option_menu = OptionMenu(self.button_frame, default_value, *list, command=command)
        option_menu.grid(row=row, column=0, sticky=tk.E, pady=(padding_top, 0))
        return option_menu

    def create_input(self, default_value, row, padding_top):
        input = tk.Entry(self.button_frame, state=DISABLED)
        input.insert(END, default_value)
        input.grid(row=row, column=0, sticky=tk.E, pady=(padding_top, 0))
        return input

    def create_button(self, text, row, padding_top, command, font = 'Helvetica 9', sticky = tk.W + tk.E):
        button = tk.Button(self.button_frame, text=text, font=font, command=command)
        button.grid(row=row, column=0, sticky=sticky, pady=(padding_top, 0))
        return button

    def create_label(self, text, row, padding_top = 10, font="Helvetica 9", sticky=tk.W):
        label = Label(self.button_frame, font=font, text=text)
        label.grid(row=row, column=0, sticky=sticky, pady=(padding_top, 0))
        return label

    def create_window_structure(self):
        for r in range(2):
            self.window.rowconfigure(r, weight=1)
        for player_2_button in range(3):
            self.window.columnconfigure(player_2_button, weight=1)

        frames = []

        for i in range(2):
            for j in range(3):
                frame = tk.Frame(self.window)
                frame.grid(row=i, column=j, rowspan=1, sticky=W + E + N + S)
                frames.append(frame)

        self.button_frame = tk.Frame(frames[0])
        self.button_frame.pack(fill=tk.X, side=tk.TOP)
        self.button_frame.columnconfigure(0, weight=1)

        for br in range(13):
            self.button_frame.rowconfigure(br, weight=1)

        self.buttons = [[None for _ in range(3)] for _ in range(3)]
        for i in range(3):
            for j in range(3):
                self.buttons[i][j] = tk.Button(frames[3], height=1, width=3, text=self.empty_text, font=('helvetica', 50),
                                               command=lambda i=i, j=j: self.game.callback(self.buttons[i][j]))
                self.buttons[i][j].grid(row=i, column=j)

        return frames

    def set_default_values_for_controls(self, frames, user_options, user_option_with_learning):
        self.used_player_1 = StringVar(frames[0])
        self.used_player_2 = StringVar(frames[0])
        self.used_file_player_1 = StringVar(frames[0])
        self.used_file_player_2 = StringVar(frames[0])

        self.clear_file_list(1)
        self.clear_file_list(2)

        self.used_player_1.set(user_options[0])
        self.used_player_2.set(user_option_with_learning[1])

    def create_controls(self, user_options, user_options_with_learning):

        hello_label = self.create_label("Hello in our Tic-Tac-Toe Game!", 0, 80, "Helvetica 10 bold", sticky=tk.W + tk.E)
        player_1_label = self.create_label("Player 1:\t", 1, 30)
        epsilon_label = self.create_label("Epsilon Value for Q-Player in learn mode:", 2, 10)
        player_1_file_label = self.create_label("File to using during game:", 3, 10)
        player_2_label = self.create_label("Player 2:\t", 4, 10)
        player_2_file_label = self.create_label("File to using during game:", 5, 10)
        iterations_label = self.create_label("Iterations number (If Two Computer Players):", 6, 20)
        self.status_label = self.create_label("Let's start the game!", 12, 20, "Helvetica 10 bold", sticky=tk.W + tk.E)

        self.new_game = self.create_button("New Game", 7, 10, command=self.game.new_game, font='Helvetica 9 bold')
        self.refresh_button = self.create_button("Refresh File List", 8, 10, command=self.refresh_file_list)
        self.clear_button = self.create_button("Clear Statistics", 9, 10, command=self.clear_stats)
        self.end_button = self.create_button("End Game", 10, 10, command=self.game.exit_game)

        self.iterations_input = self.create_input('1', 6, 20)
        self.epsilon_input = self.create_input('0.9', 2, 0)

        self.player_1_button = self.create_option_menu(1, 0, self.used_player_1, user_options_with_learning, self.player_1_change)
        self.player_2_button = self.create_option_menu(4, 20, self.used_player_2, user_options, self.player_2_change)

        self.disable_live_preview = self.create_check("Disable live preview if players are computer", 11, 10, self.change_live_preview)

    def create_plots_for_stats(self, frames):
        self.graphs = []
        self.canvases = []
        self.plot_diagram(frames[1], 1)
        self.plot_diagram(frames[4], 2)
        self.plot_stats(frames[2], 1)
        self.plot_stats(frames[5], 2)

    def clear_stats(self):
        self.game.games, self.game.game_stats, self.game.game_story = self.game.prepare_stats()
        self.update_stats()

    def player_1_change(self, value):
        self.game.change_player(value, 1)

        self.set_control_epsilon(value)
        self.set_control_for_computer_players(value)
        self.used_player_1 = value
        if (value != "Q Player in Learning Mode") & (value != "Random Player") & (value != "Human Player"):
            self.refresh_file_list(value, 1)
        else:
            self.clear_file_list(1)

    def player_2_change(self, value):
        self.game.change_player(value, 2)

        self.set_control_epsilon(value)
        self.set_control_for_computer_players(value)
        self.used_player_2 = value
        if (value != "Q Player in Learning Mode") & (value != "Random Player") & (value != "Human Player"):
            self.refresh_file_list(value, 2)
        else:
            self.clear_file_list(2)

    def player_1_file_to_learn(self, value):
        self.learn_file_1 = value

    def player_2_file_to_learn(self, value):
        self.learn_file_2 = value

    def clear_file_list(self, player):
        if player == 1:
            self.files_player_1 = ["\t\t"]
            self.used_file_player_1.set(self.files_player_1[0])
            self.draw_file_list(1)
            self.files_player_1_button.config(state=DISABLED)
        if player == 2:
            self.files_player_2 = ["\t\t"]
            self.used_file_player_2.set(self.files_player_2[0])
            self.draw_file_list(2)
            self.files_player_2_button.config(state=DISABLED)

    def refresh_file_list(self, value="", player = 0):

        if player == 1:
            if (value == "Q Player"):
                self.files_player_1 = [f for f in listdir(self.game.data_dir_path) if ((isfile(join(self.game.data_dir_path, f))) & (".p" in f))]
            elif(value == "Supervised Player"):
                self.files_player_1 = [f for f in listdir(self.game.data_dir_path) if ((isfile(join(self.game.data_dir_path, f))) & (".csv" in f))]
            self.used_file_player_1.set(self.files_player_1[0])
            self.learn_file_1 = self.files_player_1[0]
            self.draw_file_list(player)


        if player == 2:
            if (value == "Q Player"):
                self.files_player_2 = [f for f in listdir(self.game.data_dir_path) if ((isfile(join(self.game.data_dir_path, f))) & (".p" in f))]
            elif (value == "Supervised Player"):
                self.files_player_2 = [f for f in listdir(self.game.data_dir_path) if ((isfile(join(self.game.data_dir_path, f))) & (".csv" in f))]
            self.used_file_player_2.set(self.files_player_2[0])
            self.learn_file_2 = self.files_player_2[0]
            self.draw_file_list(player)

    def draw_file_list(self, player):
        if player == 1:
            self.files_player_1_button = OptionMenu(self.button_frame, self.used_file_player_1, *self.files_player_1, command=self.player_1_file_to_learn)
            self.files_player_1_button.grid(row=3, column=0, sticky=tk.E, pady=(0, 0))
        if player == 2:
            self.files_player_2_button = OptionMenu(self.button_frame, self.used_file_player_2, *self.files_player_2, command=self.player_2_file_to_learn)
            self.files_player_2_button.grid(row=5, column=0, sticky=tk.E, pady=(0, 0))

    def update_stats(self):
        bars = ["Win", "Draw", "Defeat"]

        for index, graph in enumerate(self.graphs):
            graph.clear()
            if index < 2:
                graph.set_ylabel("Scores", fontsize=9)
                graph.set_xlabel("Game numbers", fontsize=9)
                graph.set_title('Player {} results history'.format(index + 1), fontsize=9)
                self.graphs[index].plot(range(self.game.games + 1), self.game.game_story[index][0], 'r-',
                                        label='Win')
                self.graphs[index].plot(range(self.game.games + 1), self.game.game_story[index][1], 'b-',
                                        label='Draw')
                self.graphs[index].plot(range(self.game.games + 1), self.game.game_story[index][2], 'g-',
                                        label='Defeat')
                self.graphs[index].legend(loc='upper left', shadow=True)
            else:
                graph.set_ylabel("Scores", fontsize=9)
                graph.set_title('Player {} results'.format(index - 1), fontsize=9)
                self.graphs[index].bar(bars, [self.game.game_stats[index - 2][0],
                                              self.game.game_stats[index - 2][1],
                                              self.game.game_stats[index - 2][2]])

        for canvas in self.canvases:
            canvas.draw()

    def set_control_epsilon(self, value):
        if value == "Q Player in Learning Mode" :
            self.epsilon_input.config(state=NORMAL)
            self.epsilon_input.delete(0, END)
            self.epsilon_input.insert(END, '0.9')
        else:
            self.epsilon_input.config(state=DISABLED)

    def set_control_for_computer_players(self, value):
        if isinstance(self.game.player1, HumanPlayer) | isinstance(self.game.player2, HumanPlayer):
            self.disable_live_preview.config(state=DISABLED)
            self.iterations_input.config(state=DISABLED)
        else:
            self.disable_live_preview.config(state=ACTIVE)
            self.iterations_input.config(state=NORMAL)
            self.iterations_input.delete(0, END)
            self.iterations_input.insert(END, '1')

        if isinstance(self.game.player1, QPlayer) | isinstance(self.game.player1, SupervisedPlayer):
            self.files_player_1_button.config(state=NORMAL)
        else:
            self.files_player_1_button.config(state=DISABLED)

        if isinstance(self.game.player2, QPlayer) | isinstance(self.game.player2, SupervisedPlayer):
            self.files_player_2_button.config(state=NORMAL)
        else:
            self.files_player_2_button.config(state=DISABLED)
        if value == "Q Player in Learning Mode" :
            self.files_player_1_button.config(state=DISABLED)

    def plot_stats(self, frame, player):
        figure = Figure(figsize=(4, 6), dpi=100)
        graph = figure.add_subplot(111)
        graph.bar(["Win", "Draw", "Defeat"], [0, 0, 0])
        graph.set_ylabel("Scores", fontsize=9)
        graph.set_title('Player {} results'.format(player), fontsize=9)
        canvas = FigureCanvasTkAgg(figure, frame)
        canvas.get_tk_widget().pack(fill=tk.X, side=tk.BOTTOM)
        canvas.draw()
        self.graphs.append(graph)
        self.canvases.append(canvas)

    def plot_diagram(self, frame, player):
        figure = Figure(figsize=(6, 6), dpi=100)
        graph = figure.add_subplot(111)
        graph.plot(0, 0, 'r-', label='Win')
        graph.plot(0, 0, 'b-', label='Draw')
        graph.plot(0, 0, 'g-', label='Defeat')
        graph.legend(loc='upper left', shadow=True)

        graph.set_ylabel("Scores", fontsize=9)
        graph.set_xlabel("Game numbers", fontsize=9)
        graph.set_title('Player {} results history'.format(player), fontsize=9)
        canvas = FigureCanvasTkAgg(figure, frame)
        canvas.get_tk_widget().pack(fill=tk.X, side=tk.BOTTOM)
        canvas.draw()
        self.graphs.append(graph)
        self.canvases.append(canvas)

    def reset_board(self, game):
        for i in range(3):
            for j in range(3):
                self.buttons[i][j].configure(text=self.empty_text)

        self.game = game
        self.enable_board()

    def disable_board(self):
        for button_line in self.buttons:
            for button in button_line:
                button['state'] = 'disabled'
                button['bg'] = '#919191'

    def enable_board(self):
        for button_line in self.buttons:
            for button in button_line:
                button['state'] = 'active'
                button['bg'] = '#f3f3f3'

    def change_live_preview(self):
        self.live_preview_enable = not self.live_preview_enable

    def set_label_text(self, text):
        self.status_label.config(text=text)

    def set_button_mark(self, X, Y, mark):
        self.buttons[X][Y].configure(text=mark)

    def empty(self, button):
        return button["text"] == self.empty_text

    def get_iterations(self):
        try:
            iterations = int(self.iterations_input.get())
        except ValueError:
            iterations = 1
        return iterations

    def get_epsilon(self):
        try:
            epsilon = Decimal(self.epsilon_input.get())
        except ValueError:
            epsilon = 0
        if epsilon >= 1:
            epsilon = 0

        return epsilon
