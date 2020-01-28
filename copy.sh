fswatch -o gtas-parent | while read f; do
  rsync -avz -e ssh --exclude="target/" --exclude="node_modules/" --prune-empty-dirs gtas-parent 10.20.0.147:/home/aaronsolomon/gtas/GTAS
done