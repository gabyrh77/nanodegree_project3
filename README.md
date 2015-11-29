# nanodegree_project3
Project 3 from NanoDegree Android

Alexandria:

	*  Added scan functionality
	*  Updated navigation drawer implementation and design
	*  Added basic material design
	*  Added user feedback for network/server status
	*  Fix some crashes
	*  Added a search view



Football Score App:

	*  Updated the viewpager implementation and design
	*  Added basic material design
	*  Removed hardcoded season codes and added retrofit to get the seasons, teams and matches data.
	*  Added user feedback for network/server status
	*  Added a collection widget to review today's scores.
	*  Used Glide and SVG library to download, decode and show the crest images using the URL given by the API.
	*  Added RTL support.



Note: Football Score App uses Glide as a submodule, so to build:

	git clone git@github.com:bumptech/glide.git 
	cd glide  
	git submodule init && git submodule update
	./gradlew jar
