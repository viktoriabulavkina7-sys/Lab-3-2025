package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {

    // Внутренний класс для элемента списка
    private static class FunctionNode {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        FunctionNode(FunctionPoint point) {
            this.point = point;
            this.prev = null;
            this.next = null;
        }
    }

    private FunctionNode head; // выделенная голова (не хранит данные)
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;
    private int size;

    // Конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // Создаем голову
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, 0);
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this(leftX, rightX, values.length);
        FunctionNode current = head.next;
        for (int i = 0; i < values.length; i++) {
            current.point.setY(values[i]);
            current = current.next;
        }
    }

    // Вспомогательные методы для работы со списком
    private FunctionNode getNodeByIndex(int index) {
        checkIndex(index);

        // Оптимизация: если обращаемся к тому же индексу
        if (lastAccessedIndex == index && lastAccessedNode != head) {
            return lastAccessedNode;
        }

        // Оптимизация: если следующий индекс
        if (lastAccessedIndex == index - 1 && lastAccessedNode != head) {
            lastAccessedNode = lastAccessedNode.next;
            lastAccessedIndex = index;
            return lastAccessedNode;
        }

        // Оптимизация: если предыдущий индекс
        if (lastAccessedIndex == index + 1 && lastAccessedNode != head) {
            lastAccessedNode = lastAccessedNode.prev;
            lastAccessedIndex = index;
            return lastAccessedNode;
        }

        // Начинаем с ближайшего края
        FunctionNode node;
        if (index < size / 2) {
            node = head.next;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            node = head.prev;
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
        }

        lastAccessedNode = node;
        lastAccessedIndex = index;
        return node;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        newNode.prev = head.prev;
        newNode.next = head;
        head.prev.next = newNode;
        head.prev = newNode;
        size++;
        lastAccessedNode = newNode;
        lastAccessedIndex = size - 1;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > size) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне диапазона [0, " + size + "]");
        }

        if (index == size) {
            return addNodeToTail();
        }

        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null);

        newNode.prev = nextNode.prev;
        newNode.next = nextNode;
        nextNode.prev.next = newNode;
        nextNode.prev = newNode;
        size++;
        lastAccessedNode = newNode;
        lastAccessedIndex = index;

        // Обновляем индексы для элементов после вставки
        if (lastAccessedIndex >= index) {
            lastAccessedIndex++;
        }

        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        checkIndex(index);

        if (size <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: минимум 2 точки");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;
        size--;

        // Обновляем lastAccessedNode если нужно
        if (lastAccessedNode == nodeToDelete) {
            lastAccessedNode = head.next;
            lastAccessedIndex = 0;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }

        return nodeToDelete;
    }

    // Реализация интерфейса TabulatedFunction
    @Override
    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    @Override
    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        // Ищем интервал
        FunctionNode current = head.next;
        while (current != head && current.next != head && x > current.next.point.getX()) {
            current = current.next;
        }

        if (Math.abs(x - current.point.getX()) < 1e-10) {
            return current.point.getY();
        }
        if (current.next != head && Math.abs(x - current.next.point.getX()) < 1e-10) {
            return current.next.point.getY();
        }

        // Линейная интерполяция
        double x1 = current.point.getX();
        double y1 = current.point.getY();
        double x2 = current.next.point.getX();
        double y2 = current.next.point.getY();

        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    @Override
    public int getPointsCount() {
        return size;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        // Проверка порядка
        if (index > 0 && point.getX() <= node.prev.point.getX()) {
            throw new InappropriateFunctionPointException("X новой точки должен быть больше X предыдущей");
        }
        if (index < size - 1 && point.getX() >= node.next.point.getX()) {
            throw new InappropriateFunctionPointException("X новой точки должен быть меньше X следующей");
        }

        node.point = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint temp = new FunctionPoint(node.point);
        temp.setX(x);
        setPoint(index, temp);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Находим позицию для вставки
        int insertIndex = 0;
        FunctionNode current = head.next;
        while (current != head && current.point.getX() < point.getX()) {
            current = current.next;
            insertIndex++;
        }

        // Проверяем дубликат
        if (current != head && Math.abs(current.point.getX() - point.getX()) < 1e-10) {
            throw new InappropriateFunctionPointException("Точка с таким X уже существует");
        }

        // Вставляем новую точку
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне диапазона [0, " + (size - 1) + "]");
        }
    }

    @Override
    public void printFunction() {
        System.out.println("Табулированная функция (список):");
        FunctionNode current = head.next;
        while (current != head) {
            System.out.printf("(%6.2f; %6.2f)\n", current.point.getX(), current.point.getY());
            current = current.next;
        }
    }
}