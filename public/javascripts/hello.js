if (window.console) {
  // console.log("Welcome to your Play application's JavaScript!");

    function active() {
        var searchBar = document.getElementById('searchBar');

        if(searchBar.value == 'search for advisors...') {
            searchBar.value = ''
            searchBar.placeholder = 'search for advisors...'
        }
    }

}