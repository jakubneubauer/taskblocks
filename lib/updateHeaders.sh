#!/bin/bash
#
# This script updates headers of each source file and puts the Copyright
# and copying terms to them. It must be from the project's main directory.
#

HEADER='/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

'

for f in `find src -name '*.java'`; do
  echo "Modyfying $f"
  mv "$f" "$f.tmp"
  printf "$HEADER" > "$f"
  cat "$f.tmp" | awk '
    BEGIN {out=0;}
    /^\W*package/ {out=1;}
    {if(out==1) print;}
  ' >> "$f"
  rm "$f.tmp"
done
