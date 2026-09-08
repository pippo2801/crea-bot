"""
=============================================================================
🧩 MODULO RISOLUTORE LABIRINTO A GRIGLIA (bot/solver.py)
=============================================================================
Algoritmo di pathfinding e colorazione per puzzle basati su scivolamento
(come Amaze GO, Roller Splat, Paint the Maze):
- La palla scivola in linea retta finché non incontra un muro (0).
- Ogni cella percorsa durante lo scivolamento viene contrassegnata come verniciata.
- Ricerca BFS/A* con memoizzazione dello stato (posizione_palla, celle_colorate)
  per trovare la sequenza minima di swipe (UP, DOWN, LEFT, RIGHT) che colora
  tutte le celle calpestabili.
=============================================================================
"""

from collections import deque
from typing import List, Tuple, Optional, Set

DIRECTIONS = {
    "UP": (-1, 0),
    "DOWN": (1, 0),
    "LEFT": (0, -1),
    "RIGHT": (0, 1)
}

class MazeSolver:
    def __init__(self, matrix: List[List[int]], player_pos: Tuple[int, int]):
        """
        matrix: 2D array dove:
            0 = Muro / Invalicabile
            1 = Cella calpestabile da colorare
            2 = Cella calpestabile già colorata
            3 = Posizione attuale della palla
        player_pos: (r, c)
        """
        self.matrix = matrix
        self.rows = len(matrix)
        self.cols = len(matrix[0]) if self.rows > 0 else 0
        self.start_pos = player_pos

        # Raccogliamo tutte le celle calpestabili (valore != 0)
        self.walkable_cells = set()
        self.initially_painted = set()

        for r in range(self.rows):
            for c in range(self.cols):
                val = self.matrix[r][c]
                if val != 0:
                    self.walkable_cells.add((r, c))
                    if val in (2, 3):
                        self.initially_painted.add((r, c))

        # La cella iniziale è automaticamente verniciata
        self.initially_painted.add(self.start_pos)
        self.total_target_cells = len(self.walkable_cells)

    def is_valid_cell(self, r: int, c: int) -> bool:
        """Verifica se la cella è all'interno della matrice e non è un muro."""
        return 0 <= r < self.rows and 0 <= c < self.cols and self.matrix[r][c] != 0

    def simulate_roll(self, start_r: int, start_c: int, direction: str) -> Tuple[Tuple[int, int], Set[Tuple[int, int]]]:
        """
        Simula lo scivolamento della palla in una direzione fino al primo muro.
        Ritorna la nuova posizione della palla e l'insieme delle celle attraversate.
        """
        dr, dc = DIRECTIONS[direction]
        curr_r, curr_c = start_r, start_c
        passed_cells = { (curr_r, curr_c) }

        while True:
            next_r = curr_r + dr
            next_c = curr_c + dc
            if not self.is_valid_cell(next_r, next_c):
                # Trovato un muro o il bordo: la palla si arresta qui
                break
            curr_r, curr_c = next_r, next_c
            passed_cells.add((curr_r, curr_c))

        return (curr_r, curr_c), passed_cells

    def solve(self, max_depth: int = 40) -> Optional[List[str]]:
        """
        Trova la sequenza ottimale di movimenti per coprire il 100% delle celle calpestabili.
        Usa BFS (Breadth-First Search) con memorizzazione degli stati visitati.
        """
        if not self.walkable_cells:
            return []

        # Mappatura delle celle calpestabili in bitmask per altissima velocità di confronto
        cell_to_idx = {cell: i for i, cell in enumerate(sorted(self.walkable_cells))}
        all_bits = (1 << len(self.walkable_cells)) - 1

        initial_mask = 0
        for cell in self.initially_painted:
            if cell in cell_to_idx:
                initial_mask |= (1 << cell_to_idx[cell])

        # Coda BFS: (riga, colonna, bitmask_colorati, lista_mosse)
        queue = deque([(self.start_pos[0], self.start_pos[1], initial_mask, [])])
        
        # Insieme degli stati già esplorati: ((r, c), bitmask)
        visited = { ((self.start_pos[0], self.start_pos[1]), initial_mask) }

        while queue:
            curr_r, curr_c, mask, path = queue.popleft()

            # Condizione di vittoria: tutte le celle calpestabili sono verniciate!
            if mask == all_bits:
                return path

            if len(path) >= max_depth:
                continue

            for dir_name in ["UP", "DOWN", "LEFT", "RIGHT"]:
                (next_r, next_c), passed = self.simulate_roll(curr_r, curr_c, dir_name)

                # Se la mossa non sposta la palla (sbatte subito contro un muro), scartala
                if (next_r, next_c) == (curr_r, curr_c):
                    continue

                # Calcola la nuova bitmask con le celle attraversate
                new_mask = mask
                for p in passed:
                    if p in cell_to_idx:
                        new_mask |= (1 << cell_to_idx[p])

                state_key = ((next_r, next_c), new_mask)
                if state_key not in visited:
                    visited.add(state_key)
                    queue.append((next_r, next_c, new_mask, path + [dir_name]))

        # Se BFS completa senza successo (es. max depth raggiunto), usa euristica greedy
        return self._solve_greedy()

    def _solve_greedy(self, steps: int = 50) -> List[str]:
        """
        Algoritmo greedy di fallback se il labirinto è troppo grande per BFS pura.
        Privilegia la direzione che vernicia il maggior numero di celle nuove.
        """
        curr_r, curr_c = self.start_pos
        painted = set(self.initially_painted)
        path = []

        for _ in range(steps):
            if len(painted) >= len(self.walkable_cells):
                break

            best_dir = None
            max_new = -1
            best_dest = (curr_r, curr_c)
            best_passed = set()

            for dir_name in ["UP", "DOWN", "LEFT", "RIGHT"]:
                dest, passed = self.simulate_roll(curr_r, curr_c, dir_name)
                if dest == (curr_r, curr_c):
                    continue
                new_unpainted = len(passed - painted)
                if new_unpainted > max_new:
                    max_new = new_unpainted
                    best_dir = dir_name
                    best_dest = dest
                    best_passed = passed

            if best_dir is None or max_new == 0:
                # Muoviti comunque in qualsiasi direzione valida non invertita per sbloccarti
                valid_dirs = [d for d in ["UP", "DOWN", "LEFT", "RIGHT"] if self.simulate_roll(curr_r, curr_c, d)[0] != (curr_r, curr_c)]
                if not valid_dirs:
                    break
                best_dir = valid_dirs[0]
                best_dest, best_passed = self.simulate_roll(curr_r, curr_c, best_dir)

            path.append(best_dir)
            curr_r, curr_c = best_dest
            painted.update(best_passed)

        return path
