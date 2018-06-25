import pandas as pd
import numpy as np

from sklearn.ensemble import RandomForestRegressor


class SupervisedModel:

    def __init__(self, train_data):
        self.train_x = train_data.iloc[:, :-1]
        self.train_y = train_data.iloc[:, -1]
        self.model = None

    def set_model(self, model):
        self.model = model

    def train(self):
        self.model.fit(self.train_x, np.ravel(self.train_y))

    def predict(self, test_x):
        return self.model.predict(test_x)


def load_data_set(path):
    return pd.read_csv(path)


def prepare_model(dir_path, file_name):
    train_data = load_data_set(path= dir_path + file_name)

    sm = SupervisedModel(train_data)
    sm.set_model(RandomForestRegressor(n_estimators=10, random_state=35))
    sm.train()

    return sm


if __name__ == '__main__':
    sm = prepare_model("../data/", "train.csv" )
    train_data = load_data_set("../data/" + "test.csv")
    train_data = train_data.iloc[:, :-1]
    results = sm.predict(train_data)

    print(np.ravel(results))
