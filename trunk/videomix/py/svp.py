#!/usr/bin/env python

from __future__ import division
import sys
import math
from PyQt4 import QtCore
from PyQt4 import QtGui
from PyQt4.phonon import Phonon

class SimpleVideoPlayer(QtGui.QWidget):
	def __init__(self, paths, parent = None):
		QtGui.QWidget.__init__(self, parent)

		self.players = []
		source = Phonon.MediaSource(QtCore.QUrl(paths[0]))
		for path in paths:
			player = Phonon.VideoPlayer()
			player.play(Phonon.MediaSource(QtCore.QUrl(path)))
			self.players.append(player)

		layout = QtGui.QGridLayout(self)
		n = len(self.players)
		cols = int(math.ceil(math.sqrt(n)))
		for i in range(n):
			layout.addWidget(self.players[i], i // cols, i % cols)

		self.resize(800, 600)

app = QtGui.QApplication(sys.argv)
app.setApplicationName("Simple Video Player")

if len(sys.argv) < 2:
	print("Usage: " + sys.argv[0] + " FILENAMES")
	sys.exit(1)

sys.argv.pop(0)
player = SimpleVideoPlayer(sys.argv)
player.show()

sys.exit(app.exec_())
