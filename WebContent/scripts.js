"use strict";
	let view;
	let tasks;
	let currentSection;
	
	function startMain() {
		const urlParams = new URLSearchParams(location.search);
		view = urlParams.get("view");
		if(view != 'tomorrow' && view != 'someday' && view != 'fixed' && view != 'bin') {
			view = 'today';
		}		
		const user = urlParams.get('user');
		document.getElementById('user').innerHTML = user;
		const errorMessage = urlParams.get("error");
		if(errorMessage == null){
			showMainContent();
		} else {
			showError(errorMessage);
		}							
	}
	
	function showMainContent(){		
		showTopLinks();		
		document.getElementById('viewTitle').innerHTML = getViewTitle(view);
		showBottomLinks();
		showTasks();
		document.getElementById('errorMessage').style.display = 'none';
	}
	
	function changeView(newView){
		if (newView == view){
			if (currentSection == 'tasks'){
				return;
			}
			if (currentSection == 'operation'){
				cancelOperation();
				return;
			}
		}					
		view = newView;
		showMainContent();
	}
	
	function showTopLinks(){
		const viewsArray = ['today', 'tomorrow', 'someday', 'fixed', 'bin']; 
		const topLinks = document.getElementById('topLinks');
		topLinks.innerHTML = '';
		for(let itemView of viewsArray) {
			const a = document.createElement('a');
			a.setAttribute('href', "#");
			a.onclick = function(){
				changeView(itemView);
			};  
			let linkClass = view == itemView ? "activeViewLink" : "viewLink";
			a.setAttribute('class', linkClass);			
			a.innerHTML = getViewTitle(itemView);			
			topLinks.appendChild(a);
		}
	}	
	
	function showBottomLinks(){
		const addTaskLinks = document.getElementsByClassName('addTaskLink');
		for(let addTaskLink of addTaskLinks){
			addTaskLink.innerHTML = '';
			if(view == 'today' || view == 'tomorrow' || view == 'someday') {					
				addBottomLink(addTaskLink, function(){addTask()}, 'Add Task');					
			}				
		}
		const bottomLinks = document.getElementById('bottomLinks');
		bottomLinks.innerHTML = '';
		switch(view) {					
		case 'fixed':
			addBottomLink(bottomLinks, function(){doOperation('remove')}, 'Remove');					
			break;
		case 'bin':					
			addBottomLink(bottomLinks, function(){doOperation('restore')}, 'Restore');
			addBottomLink(bottomLinks, function(){doOperation('delete')}, 'Delete');
			addBottomLink(bottomLinks, function(){emptyBin()}, 'Empty Bin');					
			break;
		default:				
			addBottomLink(bottomLinks, function(){doOperation('fix')}, 'Fix');
			addBottomLink(bottomLinks, function(){doOperation('remove')}, 'Remove');					
		}		
	}
	
	async function showTasks(){
		showBlocks('block', 'none', 'none');			
		const url = `main?view=${view}`;	
		let response = await fetch(url);
		if (!response.ok){
			showError("HTTP error: " + response.status);
			return;
		}		
		tasks = await response.json();
		if (JSON.stringify(tasks) == '[1]'){
				showError('Problem while connecting to data source');
				return;
		}			
		if(tasks.length == 0) {
			document.getElementById('tasks-empty').style.display = 'block';
			document.getElementById('tasks-not-empty').style.display = 'none';
			document.getElementById('currentView').innerHTML = view;				
		} else {				
			document.getElementById('tasks-empty').style.display = 'none';
			document.getElementById('tasks-not-empty').style.display = 'block';				
			const tasksTable = document.getElementById("tasks-list");				
			tasksTable.innerHTML = '';				
			if(view == 'today' || view == 'tomorrow') {
				document.getElementById('tasksTableDate').style.display = 'none';
			} else{
				document.getElementById('tasksTableDate').style.display = 'block';
			}				
			for(let task of tasks){
				const tr = document.createElement('tr');					
				const td1 = document.createElement('td')
				const input = document.createElement('input');				
				input.setAttribute('type', 'checkbox');
				input.setAttribute('name', 'idTask');
				input.setAttribute('value', task.id);
				td1.appendChild(input);
				tr.appendChild(td1);					
				const td2 = document.createElement('td');
				td2.innerHTML = task.text;
				tr.appendChild(td2);
				if(view == 'someday' || view == 'fixed' || view == 'bin') {
					const td3 = document.createElement('td');
					td3.innerHTML = task.date;
					tr.appendChild(td3);
				}
				const td4 = document.createElement('td');
				td4.setAttribute('class', 'tdFile');	
				if (task.fileStatus === '1'){
					td4.innerHTML = task.file;
					const aDownload = document.createElement('a');
					aDownload.setAttribute('href', `download?idTask=${task.id}&view=${view}`);
					aDownload.innerHTML = 'Download';
					td4.appendChild(aDownload);						
					const aDelete = document.createElement('a');
					aDelete.setAttribute('href', '#');
					aDelete.onclick = function(){
						deleteFileById(task.id);
				    }									
					aDelete.innerHTML = 'Delete';	
					td4.appendChild(aDelete);
				} else{
					const aUpload = document.createElement('a');
					aUpload.setAttribute('href', '#');
					aUpload.onclick = function(){
						upload(task.id);
				   	 }									
					aUpload.innerHTML = 'Upload';	
					td4.appendChild(aUpload);
				}
				tr.appendChild(td4);					
				tasksTable.appendChild(tr);					
			}							
		}
		currentSection = 'tasks';
	}
	
	function showError(errorMessage){
		showTopLinks();
		showBlocks('none', 'none', 'none');
		document.getElementById('viewTitle').innerHTML = 'Error';
		document.getElementById('errorMessage').innerHTML = errorMessage;
		document.getElementById('errorMessage').style.display = 'block';
		currentSection = 'error';
	}
	
	function addBottomLink(whereToAdd, func, text){
		const a = document.createElement('a');
		a.setAttribute('href', '#');
		a.onclick = func;    	
		a.setAttribute('class', 'bottomLinks');			
		a.innerHTML = text;			
		whereToAdd.appendChild(a);
	}
	
	function addTask(){	
		showBlocks('none', 'none', 'block');
		const datepicker = document.getElementById('datepicker');
		switch(view){
		case 'today':
			datepicker.setAttribute('value', getSlashDate(new Date()));							
			break;
		case 'tomorrow':
			datepicker.setAttribute('value', getSlashDate(getTomorrowDate()));			
		}
		currentSection = 'operation';
	}
	
	function upload(idTask){
		showBlocks('none', 'block', 'none');		
		let task;
		for(let t of tasks){
			if(t.id == idTask){
				task = t;
			}
		}
		document.getElementById('uploadDate').innerHTML = task.date;
		document.getElementById('uploadText').innerHTML = task.text;
		document.uploadFile.idTask.value = idTask;
		document.uploadFile.view.value = view;
		currentSection = 'operation';
	}
	
	function cancelOperation(){
		showBlocks('block', 'none', 'none');
		currentSection = 'tasks';
	}
	
	function showBlocks(tasksList, upload, addTask){
		document.getElementById('tasksList').style.display = tasksList;
		document.getElementById('upload').style.display = upload;
		document.getElementById('addTask').style.display = addTask;
	}
	
	function getViewTitle(view) {
		let title;
		switch(view) {
		case 'today':
			title = 'Today ' + getShortDate(new Date());
			break;
		case 'tomorrow':
			title = 'Tomorrow ' + getShortDate(getTomorrowDate());
			break;
		case 'someday':
			title = 'Someday';
			break;
		case 'fixed':
			title = 'Fixed';
			break;
		case 'bin':
			title = 'Recycle Bin';
			break;		
		}
		return title;		
	}
	
	function getSlashDate(date){
		const month = date.getMonth() + 1;
		const day = date.getDate();
		const year = date.getFullYear();
		return getTwoDigits(month) + '/' + getTwoDigits(day) + '/' + year;
	}
	
	function getShortDate(date){
		const day = date.getDate();
		const month = date.getMonth() + 1;
		return getTwoDigits(day) + '.' + getTwoDigits(month);
	}
	
	function getTomorrowDate() {
		let tomorrow = new Date();
		tomorrow.setDate(tomorrow.getDate() + 1);		
		return tomorrow;
	}
	
	function getTwoDigits(x){
		return x < 10 ? '0' + x : x;
	}	
	
	function logout() {
		document.logout.submit();
	}
	
	function toggle(box) {			
		if(typeof document.tasks.idTask.length == 'undefined'){
			document.tasks.idTask.checked = box.checked;		
		}
		else{
			for (let i = 0; i < document.tasks.idTask.length; i++) {
				document.tasks.idTask[i].checked = box.checked;
			}
		}		
	}

	function doOperation(operationName) {
		const err = document.getElementById("errorText");
		let noTaskSelected = true;
		if(typeof document.tasks.idTask.length == 'undefined'){
			noTaskSelected = !document.tasks.idTask.checked;			
		}
		else{
			for (let i = 0; i < document.tasks.idTask.length; i++) {
				if (document.tasks.idTask[i].checked) {					
					noTaskSelected = false;
				}
			}
		}		
		if (noTaskSelected) {
			err.innerHTML = "No task is selected";
			return;
		}		
		document.tasks.operation.value = operationName;
		document.tasks.view.value = view;
		document.tasks.submit();
	}

	function emptyBin() {		
		if(typeof document.tasks.idTask.length == 'undefined'){
			document.tasks.idTask.checked = true;			
		}
		else{
			for (let i = 0; i < document.tasks.idTask.length; i++) {
				document.tasks.idTask[i].checked = true;
			}
		}					
		document.tasks.operation.value = 'delete';
		document.tasks.view.value = view;	
		document.tasks.submit();		
	}
	
	function deleteFileById(id) {		
		document.deleteFile.idTask.value = id;
		document.deleteFile.view.value = view;
		document.deleteFile.submit();		
	}
	
	function sendFile() {
		const err = document.getElementById("errorFile");
		err.innerHTML = '';
		if(document.uploadFile.file.value === ''){
			err.innerHTML = "No file is selected";
			return;
		};
		document.uploadFile.submit();
	}
	
	function sendTask() {
		const err = document.getElementById("errorAddTask");
		const text = document.addTask.text.value.trim();
		if (text === "") {
			err.innerHTML = "Text is empty";
			return;
		}
		
		const date = document.addTask.date.value;
		const time = Date.parse(date);
		if (isNaN(time)) {
			err.innerHTML = "Incorrect date";
			return;
		}		
		document.addTask.view.value = view;
		document.addTask.submit();
	}
	
	function checkError() {
		const urlParams = new URLSearchParams(location.search);
		const strError = urlParams.get("error");
		if(strError) {
			document.getElementById('error').innerHTML = strError;
		}
	}
	
	function login() {
		document.getElementById('error').innerHTML = '';
		const err = document.getElementById("errorText");
		const account = document.login.account.value.trim();
		if (account === "") {
			err.innerHTML = "Login is empty";
			return;
		}
		const password = document.login.password.value;
		if (password === "") {
			err.innerHTML = "Password is empty";
			return;
		}
		document.login.submit();
	}
	
	function register() {
		document.getElementById('error').innerHTML = '';
		const err = document.getElementById("errorText");
		const account = document.register.account.value.trim();
		if (account === "") {
			err.innerHTML = "Login is empty";
			return;
		}
		const password = document.register.password.value;
		const confirmPassword = document.register.confirmPassword.value;
		if (password != confirmPassword) {
			err.innerHTML = "Enter the same password";
			return;
		}
		if (password === "") {
			err.innerHTML = "Password is empty";
			return;
		}
		document.register.submit();
	}