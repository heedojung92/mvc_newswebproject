function generateChartWithArr(chart,arr,div_id,title_text,data_type){
	dps=[];
	for(var key in arr){
		dps.push({label:key,y:Number(arr[key])});
		}
	chart = new CanvasJS.Chart(div_id, {
		animationEnabled: true,
		animationDuration: 2000,
		title:{
			text: title_text,
			fontSize: 25,
			},
			data: [              
				{
					type: data_type,
					dataPoints: dps
					}]
			});
	chart.render();
};