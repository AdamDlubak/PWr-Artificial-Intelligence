import tkinter as tk

from src.game import Game
from src.players.human_player import HumanPlayer
from src.players.random_player import RandomPlayer

if __name__ == '__main__':

    data_dir_path = "../data"

    window = tk.Tk()
    default_player_1 = HumanPlayer(mark="X")
    default_player_2 = RandomPlayer(mark="O")
    game = Game(window, default_player_1, default_player_2, data_dir_path)

    window.mainloop()