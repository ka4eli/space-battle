(function (window, document, $) {

    var BOARD_ROWS = 16;
    var BOARD_COLUMNS = 16;

    var SERVER = 'http://localhost:8080/xl-spaceship';
    var game_id = "game_820";
    var user_id = "";
    var isPlaying = false;

    function generateBoard(cl, b) {
        var z = b.map(function (c) {
            return c.split("");
        });

        var board = [];

        for (var i = 0; i < BOARD_ROWS; i++) {
            var rows = [];
            for (var j = 0; j < BOARD_COLUMNS; j++) {
                if (z[i][j] == "*") {
                    rows.push('<div data-x="' + i + '" data-y="' + j + '" class="row-item ship unclickable" id="' + i + 'x' + j + '"></div>');
                } else if (z[i][j] == "X") {
                    rows.push('<div data-x="' + i + '" data-y="' + j + '" class="row-item killed unclickable" id="' + i + 'x' + j + '"></div>');
                } else if (z[i][j] == "-") {
                    rows.push('<div data-x="' + i + '" data-y="' + j + '" class="row-item missed unclickable" id="' + i + 'x' + j + '"></div>');
                } else {
                    rows.push('<div data-x="' + i + '" data-y="' + j + '" class="row-item' + cl + '" id="' + i + 'x' + j + '"></div>');

                }
            }

            board.push('<div class="rows">' + rows.join('') + '</div>')
        }

        return board.join('');
    }

    function getState(id) {
        return $.ajax({
            url: SERVER + '/user/game/' + id
        })
    }

    function init() {
        var $player = $('#player');
        var $enemy = $('#enemy');
        var $fireButton = $('#fire');

        var fires = {};

        getState(game_id).then(function (responce) {
            $player.append(generateBoard(" unclickable", responce.self.board));
            $enemy.append(generateBoard(" clickable", responce.opponent.board));
        });

        function onFireButtonClicked() {
            console.log(fires);
            var resp = $.ajax({
                method: 'PUT',
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                url: SERVER + '/user/game/' + game_id + '/fire',
                data: JSON.stringify({
                    salvo: _.keys(fires)
                })
            });
            resp.then(function (r) {
                getState(game_id).then(function (responce) {
                    if (typeof responce.game.won !== 'undefined') {
                        var $winner = $('#winner');
                        $winner.append('<p>Game Over. The winner is ' + responce.game.won + ' </p>')
                        isPlaying = false;
                    }
                    $enemy.empty();
                    $enemy.append(generateBoard(" clickable", responce.opponent.board));
                    fires = {};
                    setTimeout(refreshPlayer, 1000);
                });
            });
        }

        function onPlayerItemClicked() {
            console.log('arguments', arguments);
        }

        function onEnemyItemClicked(event) {
            var $target = $(event.target);
            var x = intToHex($target.data('x'));
            var y = intToHex($target.data('y'));

            //if ($target.hasClass('row-item')) {
            if ($target.hasClass('clickable')) {
                $target.toggleClass('active');
                if ($target.hasClass('active')) {
                    fires[x + 'x' + y] = [x, y];
                } else {
                    delete fires[x + 'x' + y];
                }
                console.log(fires);
            }
        }

        function refreshPlayer() {
            getState(game_id).then(function (responce) {
                $player.empty();
                $player.append(generateBoard(" clickable", responce.self.board));
                fires = {};
            });
            //setTimeout(refreshPlayer, 500); // you could choose not to continue on failure...
        }

        //$enemy.append(generateBoard(" clickable"));

        $fireButton.on('click', onFireButtonClicked);

        $player.on('click', onPlayerItemClicked);
        $enemy.on('click', onEnemyItemClicked);

        //$enemy.find('#7x0')
        //setTimeout(refreshPlayer, 500);
    }

    function waiting() {
        var $hello = $('#hello');
        var $available_games = $('#available_games');
        var $fireButton = $('#fire');
        $fireButton.hide();
        $hello.append('<p>Waiting for game</p>')
        $available_games.append('<a href="#">game_821</a>')
        $available_games.on('click', onGameJoin);


        function onGameJoin(event) {
            var $target = $(event.target);
            console.log($target);
            game_id = $target[0].innerHTML;
            $hello.empty();
            $available_games.empty();
            $fireButton.show();
            isPlaying = true;
            init();

        }
    }

    $(document).ready(function () {
        if (isPlaying) {
            init();
        } else {
            waiting();
        }
    });

})(window, document, $);