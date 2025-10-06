## Parallel Boolean Circuits

This project implements a system for parallel evaluation of boolean expressions. The expressions are represented as trees where nodes can be basic logical operators (AND, OR, NOT), conditional statements (IF), or threshold operators (GT, LT). 
The main goal is to calculate multiple expressions simultaneously and process their subexpressions in parallel. 
The system handles this through a CircuitSolver interface, which provides methods to start calculations and stop them when needed. 
Each calculation can be performed independently, and the results can be retrieved when ready. 
The implementation ensures safe concurrent operation and proper handling of interruptions, 
maximizing the efficiency of parallel processing while maintaining the correctness of results.
