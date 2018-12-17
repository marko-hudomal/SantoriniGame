package com.example.markohudomal.santorini.algorithm;

public class Extras {

//    //Node
//    public class Node{
//        private Cell[][] board;
//        private Point pointMove;
//        private Point pointBuild;
//        private ArrayList<Point> figures1;
//        private ArrayList<Point> figures2;
//
//        public ArrayList<Node> childs= new ArrayList<>();
//
//        public Node(Cell[][] board, Point pointMove, Point pointBuild, ArrayList<Point> figures1, ArrayList<Point> figures2) {
//            this.board = board;
//            this.pointMove = pointMove;
//            this.pointBuild = pointBuild;
//            this.figures1 = figures1;
//            this.figures2 = figures2;
//        }
//
//        public Cell[][] getBoard() {
//            return board;
//        }
//
//        public void setBoard(Cell[][] board) {
//            this.board = board;
//        }
//
//        public Point getPointMove() {
//            return pointMove;
//        }
//
//        public void setPointMove(Point pointMove) {
//            this.pointMove = pointMove;
//        }
//
//        public Point getPointBuild() {
//            return pointBuild;
//        }
//
//        public void setPointBuild(Point pointBuild) {
//            this.pointBuild = pointBuild;
//        }
//
//        public ArrayList<Point> getFigures1() {
//            return figures1;
//        }
//
//        public void setFigures1(ArrayList<Point> figures1) {
//            this.figures1 = figures1;
//        }
//
//        public ArrayList<Point> getFigures2() {
//            return figures2;
//        }
//
//        public void setFigures2(ArrayList<Point> figures2) {
//            this.figures2 = figures2;
//        }
//
//        public ArrayList<Node> getChilds() {
//            return childs;
//        }
//
//        public void setChilds(ArrayList<Node> childs) {
//            this.childs = childs;
//        }
//    }
//    //--------------------------------------------------------------------------
//
//    private Node root;
//
//    public void initalizeTree(Cell[][] matrix,int depth)
//    {
//        ArrayList<Point> f1 = new ArrayList<>();
//        ArrayList<Point> f2 = new ArrayList<>();
//
//        //Add figures positions to root
//        for(int i = 0; i<GameActivity.BOARD_WIDTH;i++)
//        {
//            for(int j = 0; j<GameActivity.BOARD_WIDTH;j++)
//            {
//                if (matrix[i][j].getPlayer()==0){
//                    f1.add(new Point(i,j));
//                }else if (matrix[i][j].getPlayer()==1){
//                    f2.add(new Point(i,j));
//                }
//            }
//        }
//        root = new Node(copyBoardBlank(matrix),new Point(0,0),new Point(0,0),f1,f2);
//
//
//    }
}
