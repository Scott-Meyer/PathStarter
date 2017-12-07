#lang racket

;;;Every program has two key peices of data:
;;;    The path to the exe to run it
;;;    The name of the process acording to windows
(struct Program (path name))

;;;Temperarally hardcode some programs.
;;;Path of exile
(define poe (Program "C:/games/poe/path.exe" "PathOfExile.exe"))
;;;List of things to run with path of exile
(define poe-assistants (list
                        (Program "script/myscript.exe" "AHK.exe")
                        (Program "trader/trademacro.exe" "Tradem.exe")))


;;;Start a given program
(define (start-p program)
  #t)

;;;Close a program
(define (close-p program)
  #t)

;;;Check if a program is running
(define (is-running? program)
  #t)

;;;Check if a program is up to date.
(define (is-uptodate? program)
  #t)

;;;Start all programs.
(define (start-all programs)
  (map start-p programs))

;;;Main function, run when the executable is opened.
;;;   Opens programs, loops to see if poe is open, then closes everything.
(define main

  ;;Do all of the following (iterative time)
  (begin
    (start-all (cons poe poe-assistants))
    (while (is-running? poe)))