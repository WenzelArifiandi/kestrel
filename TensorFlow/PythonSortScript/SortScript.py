# 1. Find every file in a given folder
# 2. Take the first part of each filename, and use
#    that as the folder name
# 3. If the folder doesn't exist, create it
# 4. Move the file into that folder
# 5. Repeat

folder_path = "C:\Dev\Massey Dataset\handgesturedataset_part1 - Copy"

# 1. Find every file in a given folder
import os
import os.path
import re
import shutil

images = [f for f in os.listdir(folder_path) if os.path.isfile(os.path.join(folder_path, f))]

for image in images:
    folderName = images[-6:-4]

    if not os.path.exists(folderName):
        os.mkdir(folderName)
        shutil.copy(os.path.join('folder', f), folderName)
    else:
        shutil.copy(os.path.join('folder', f), folderName)