# Sudoku4j
A small free application which can solve simple Sudokus.

# ----- USAGE -----

0.Build the process on your own.

1.Put a plain text file named "sudoku.txt" which contains an unsolved sudoku in the same folder with the jar file.

2.Create a batch file to launch the application. (Just like what we do to start Sponge or Spigot Minecraft server.)

The requirement of the format of "sudoku.txt" is as follows:

/.\Make sure there are 9 numbers in one line. '0' represents an unsolved number.

For example: (this one is what I used for debug)
┏━┯━┯━┳━┯━┯━┳━┯━┯━┓
┃0│0│1┃9│0│0┃0│0│0┃
┠─┼─┼─╂─┼─┼─╂─┼─┼─┨
┃0│2│0┃5│0│3┃0│9│0┃
┠─┼─┼─╂─┼─┼─╂─┼─┼─┨
┃0│9│5┃0│0│0┃2│6│0┃
┣━┿━┿━╋━┿━┿━╋━┿━┿━┫
┃4│0│8┃0│1│0┃0│2│0┃
┠─┼─┼─╂─┼─┼─╂─┼─┼─┨
┃0│0│2┃0│6│0┃0│0│4┃
┠─┼─┼─╂─┼─┼─╂─┼─┼─┨
┃0│7│0┃0│9│0┃0│0│3┃
┣━┿━┿━╋━┿━┿━╋━┿━┿━┫
┃2│0│0┃8│0│0┃0│0│0┃
┠─┼─┼─╂─┼─┼─╂─┼─┼─┨
┃5│0│0┃0│0│1┃9│0│2┃
┠─┼─┼─╂─┼─┼─╂─┼─┼─┨
┃7│0│0┃0│0│9┃3│0│0┃
┗━┷━┷━┻━┷━┷━┻━┷━┷━┛
