import spacy
from spacy.pipeline import EntityRuler
import csv
with open('./med1.csv','r', encoding='utf8', errors ='ignore') as file:
# file = open("./med1.csv", "r")
    data = list(csv.reader(file, delimiter=","))
file.close()
names= [row[4].split()[0].lower() for row in data]
description = [row[2].lower() for row in data]
print(names)
#
# import os
# def main():
#     dir_list = os.listdir("./")
#     print(dir_list)
#
def main():
    nlp=spacy.blank("en")
    ruler = EntityRuler(nlp)
    nlp.add_pipe(ruler)
    words=names[1:]
    patterns=[]
    for word in words:
        patterns.append({"label":"MED", "pattern":[{"LOWER":word.lower()}]})
    ruler.add_patterns(patterns)
    doc=nlp(txt)
    rs=[]
    for ent in doc.ents:
        res = ent.text
    return "res"
