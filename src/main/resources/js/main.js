(function (window, document, $) {

    var BOARD_ROWS = 16;
    var BOARD_COLUMNS = 16;

    var SERVER = 'http://localhost:8080/xl-spaceship';

    function generateBoard(cl) {
        var board = [];

        for (var i = 0; i < BOARD_ROWS; i++) {
            var rows = [];
            for (var j = 0; j < BOARD_COLUMNS; j++) {
                rows.push('<div data-y="' + i + '" data-x="' + j + '" class="row-item' + cl + '" id="' + i + 'x' + j + '"></div>');
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

        getState('game_3241').then(function (responce) {
            //responce.self.board
            console.log(responce.self.board);

            $player.append(generateBoard(""/*responce.self.board*/));
        });

        function onFireButtonClicked() {
            console.log(fires);
            var s = ["1x1","2x2"];
            $.ajax({
                method: 'PUT',
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                url: SERVER + '/user/game/game_3241/fire',
                data: JSON.stringify({
                    salvo: _.keys(fires)
                })
            });
        }

        function onPlayerItemClicked() {
            console.log('arguments', arguments);
        }

        function onEnemyItemClicked(event) {
            var $target = $(event.target);
            var x = $target.data('x');
            var y = $target.data('y');

            //if ($target.hasClass('row-item')) {
            if ($target.hasClass('clickable')) {
                $target.toggleClass('active');
                                           console.log("YESS")
                fires[x + 'x' + y] = [x, y];
            }
        }

        $enemy.append(generateBoard(" clickable"));

        $fireButton.on('click', onFireButtonClicked);

        $player.on('click', onPlayerItemClicked);
        $enemy.on('click', onEnemyItemClicked);

        //$enemy.find('#7x0')
    }

    $(document).ready(function () {
        init();
    });

})(window, document, $);