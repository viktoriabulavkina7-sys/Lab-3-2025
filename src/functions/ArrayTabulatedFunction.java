package functions;

public class ArrayTabulatedFunction implements TabulatedFunction {
    private FunctionPoint[] points;
    private int size;
    private int capacity;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        capacity = Math.max(pointsCount * 2, 10);
        points = new FunctionPoint[capacity];
        size = pointsCount;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        this(leftX, rightX, values.length);
        for (int i = 0; i < values.length; i++) {
            points[i].setY(values[i]);
        }
    }

    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    @Override
    public double getRightDomainBorder() {
        return points[size - 1].getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        int i = 0;
        while (i < size - 1 && x > points[i + 1].getX()) {
            i++;
        }

        if (Math.abs(x - points[i].getX()) < 1e-10) {
            return points[i].getY();
        }
        if (i < size - 1 && Math.abs(x - points[i + 1].getX()) < 1e-10) {
            return points[i + 1].getY();
        }

        double x1 = points[i].getX();
        double y1 = points[i].getY();
        double x2 = points[i + 1].getX();
        double y2 = points[i + 1].getY();

        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    @Override
    public int getPointsCount() {
        return size;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(points[index]);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);

        if (index > 0 && point.getX() <= points[index - 1].getX()) {
            throw new InappropriateFunctionPointException("X новой точки должен быть больше X предыдущей");
        }
        if (index < size - 1 && point.getX() >= points[index + 1].getX()) {
            throw new InappropriateFunctionPointException("X новой точки должен быть меньше X следующей");
        }

        points[index] = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        checkIndex(index);
        return points[index].getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        FunctionPoint temp = new FunctionPoint(points[index]);
        temp.setX(x);
        setPoint(index, temp);
    }

    @Override
    public double getPointY(int index) {
        checkIndex(index);
        return points[index].getY();
    }

    @Override
    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y);
    }

    @Override
    public void deletePoint(int index) {
        checkIndex(index);

        if (size <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: минимум 2 точки");
        }

        System.arraycopy(points, index + 1, points, index, size - index - 1);
        size--;

        if (size < capacity / 4 && capacity > 10) {
            resize(capacity / 2);
        }
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = 0;
        while (insertIndex < size && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }

        if (insertIndex < size && Math.abs(points[insertIndex].getX() - point.getX()) < 1e-10) {
            throw new InappropriateFunctionPointException("Точка с таким X уже существует");
        }

        if (size == capacity) {
            resize(capacity * 2);
        }

        System.arraycopy(points, insertIndex, points, insertIndex + 1, size - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        size++;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне диапазона [0, " + (size - 1) + "]");
        }
    }

    private void resize(int newCapacity) {
        FunctionPoint[] newPoints = new FunctionPoint[newCapacity];
        System.arraycopy(points, 0, newPoints, 0, size);
        points = newPoints;
        capacity = newCapacity;
    }

    @Override
    public void printFunction() {
        System.out.println("Табулированная функция (массив):");
        for (int i = 0; i < size; i++) {
            System.out.printf("(%6.2f; %6.2f)\n", points[i].getX(), points[i].getY());
        }
    }
}