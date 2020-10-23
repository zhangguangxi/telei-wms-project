#!/bin/bash
svn update
git pull
git add .
git commit -m "init project"
git push