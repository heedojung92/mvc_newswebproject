function userInterestNews(divId){
	var container = document.getElementById(divId);

	var dateChoiceDiv = document.createElement("DIV");
	dateChoiceDiv.id = "date_choice";
	dateChoiceDiv.style.cssText = "width: 1200px; height: 70px; border: 1px solid #ccc; display:flex; flex-direction: row;";
	
	var dateStartDiv=document.createElement("DIV");
	dateStartDiv.id="date_start";
	dateStartDiv.innerHTML="<h3>Date Start: </h3>";
	dateStartDiv.style.cssText = "width: 600px; height: 70px; border: 1px solid #ccc;";
	
	var dateStartInput=document.createElement("INPUT");
	dateStartInput.type="date";
	dateStartInput.id="date_start_input";
	
	dateStartDiv.appendChild(dateStartInput);
	
	
	
	var dateEndDiv=document.createElement("DIV");
	dateEndDiv.id="date_end";
	dateEndDiv.innerHTML="<h3>Date End: </h3>";
	dateStartDiv.style.cssText = "width: 600px; height: 70px; border: 1px solid #ccc;";
	
	var dateEndInput=document.createElement("INPUT");
	dateEndInput.type="date";
	dateEndInput.id="date_end_input";
	
	dateEndDiv.appendChild(dateEndInput);
	
	dateChoiceDiv.appendChild(dateStartDiv);
	dateChoiceDiv.appendChild(dateEndDiv);
	//dateChoiceDiv end
	
	
	var tagContainer=document.createElement("DIV");
	tagContainer.id="tag_container";
	tagContainer.style.cssText = "width: 1200px; height: 500px; border: 1px solid #ccc; display:flex; flex-direction: row;";
	
	
	var hashtagContainer=document.createElement("DIV");
	hashtagContainer.id="hashtag_container";
	hashtagContainer.style.cssText = "width: 600px; height: 500px; border: 1px solid #ccc;";
	
	var hashtagTitle=document.createElement("DIV");
	hashtagTitle.id="hashtag_title";
	hashtagTitle.innerHTML="HashTag Of Interest";
	hashtagTitle.style.cssText="width: 600px; height: 50px; border: 1px solid #ccc;";
		
	var hashtagBox=document.createElement("DIV");
	hashtagBox.id="hashtag_box";
	hashtagBox.style.cssText="width: 600px; height: 450px; border: 1px solid #ccc;";
	
	hashtagContainer.appendChild(hashtagTitle);
	hashtagContainer.appendChild(hashtagBox);
	
	
	//hashtagContainer end
	
	
	var comptagContainer=document.createElement("DIV");
	comptagContainer.id="comptag_container";
	comptagContainer.style.cssText = "width: 600px; height: 500px; border: 1px solid #ccc;";
	
	var comptagTitle=document.createElement("DIV");
	comptagTitle.id="comptag_title";
	comptagTitle.innerHTML="CompTag Of Interest";
	comptagTitle.style.cssText="width: 600px; height: 50px; border: 1px solid #ccc;";
		
	var comptagBox=document.createElement("DIV");
	comptagBox.id="comptag_box";
	comptagBox.style.cssText="width: 600px; height: 450px; border: 1px solid #ccc;";
	
	comptagContainer.appendChild(comptagTitle);
	comptagContainer.appendChild(comptagBox);
	
	//comptagContainer end
	
	
	tagContainer.appendChild(hashtagContainer);
	tagContainer.appendChild(comptagContainer);
	//tagContainer end

	var newsContainer=document.createElement("DIV");
	newsContainer.id="news_container";
	newsContainer.style.cssText = "width: 1200px; height: 600px; border: 1px solid #ccc; display:flex; flex-direction: row;";
	
	
	var newsStatContainer=document.createElement("DIV");
	newsStatContainer.id="newsstat_container";
	newsStatContainer.style.cssText = "width: 600px; height: 600px; border: 1px solid #ccc;";
	
	var newsStatTitle=document.createElement("DIV");
	newsStatTitle.id="newsstat_title";
	newsStatTitle.innerHTML="News Stat Title";
	newsStatTitle.style.cssText="width: 600px; height: 50px; border: 1px solid #ccc;";
	
	var newsStatBox=document.createElement("DIV");
	newsStatBox.id="newsstat_box";
	newsStatBox.innerHTML="News Stat Box";
	newsStatBox.style.cssText="width: 600px; height: 550px; border: 1px solid #ccc;";
	
	newsStatContainer.appendChild(newsStatTitle);
	newsStatContainer.appendChild(newsStatBox);
	
	//newsstat_container end
	
	
	
	
	
	
	var newsListContainer=document.createElement("DIV");
	newsListContainer.id="newslist_container";
	newsListContainer.style.cssText = "width: 600px; height: 600px; border: 1px solid #ccc;";
	
	var newsListTitle=document.createElement("DIV");
	newsListTitle.id="newslist_title";
	newsListTitle.innerHTML="News List Title";
	newsListTitle.style.cssText="width: 600px; height: 50px; border: 1px solid #ccc;";
	
	var newsListBox=document.createElement("DIV");
	newsListBox.id="newslist_box";
	newsListBox.innerHTML="News List Box";
	newsListBox.style.cssText="width: 600px; height: 550px; border: 1px solid #ccc;";
	
	newsListContainer.appendChild(newsListTitle);
	newsListContainer.appendChild(newsListBox);
	
	//newsListContainer end
	
	
	newsContainer.appendChild(newsStatContainer);
	newsContainer.appendChild(newsListContainer);
	
	
	container.appendChild(dateChoiceDiv);
	container.appendChild(tagContainer);
	container.appendChild(newsContainer);


	

	
}