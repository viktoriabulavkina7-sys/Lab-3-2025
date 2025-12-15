import functions.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ ARRAY TABULATED FUNCTION ===\n");
        testArrayFunction();

        System.out.println("\n=== ТЕСТИРОВАНИЕ LINKED LIST TABULATED FUNCTION ===\n");
        testLinkedListFunction();

        System.out.println("\n=== ТЕСТИРОВАНИЕ ИСКЛЮЧЕНИЙ ===\n");
        testExceptions();
    }

    private static void testArrayFunction() {
        try {
            double[] values = {0, 1, 4, 9, 16, 25};
            TabulatedFunction func = new ArrayTabulatedFunction(0, 5, values);
            func.printFunction();

            System.out.println("\nЗначение в точке 2.5: " + func.getFunctionValue(2.5));

            // Тест добавления точки
            func.addPoint(new FunctionPoint(2.5, 6.25));
            System.out.println("После добавления точки (2.5, 6.25):");
            func.printFunction();

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void testLinkedListFunction() {
        try {
            double[] values = {0, 1, 4, 9, 16, 25};
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 5, values);
            func.printFunction();

            System.out.println("\nЗначение в точке 2.5: " + func.getFunctionValue(2.5));

            // Тест добавления точки
            func.addPoint(new FunctionPoint(2.5, 6.25));
            System.out.println("После добавления точки (2.5, 6.25):");
            func.printFunction();

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void testExceptions() {
        System.out.println("1. Тест некорректного конструктора:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(5, 0, 5); // Левая граница > правой
        } catch (IllegalArgumentException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("\n2. Тест выхода за границы массива:");
        try {
            double[] values = {0, 1, 4};
            TabulatedFunction func = new ArrayTabulatedFunction(0, 2, values);
            func.getPoint(10); // Несуществующий индекс
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("\n3. Тест некорректной точки:");
        try {
            double[] values = {0, 1, 4, 9};
            TabulatedFunction func = new ArrayTabulatedFunction(0, 3, values);
            func.setPoint(1, new FunctionPoint(2.5, 6.25)); // Нарушает порядок
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("\n4. Тест удаления при минимальном количестве точек:");
        try {
            double[] values = {0, 1, 4};
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 2, values);
            func.deletePoint(0);
            func.deletePoint(0); // Пытаемся удалить вторую точку
        } catch (IllegalStateException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("\n5. Тест дублирования точки:");
        try {
            double[] values = {0, 1, 4};
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 2, values);
            func.addPoint(new FunctionPoint(1, 2)); // X уже существует
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("\n6. Тест совместимости интерфейса:");
        try {
            // Используем интерфейсный тип
            TabulatedFunction func1 = new ArrayTabulatedFunction(0, 2, new double[]{0, 1, 4});
            TabulatedFunction func2 = new LinkedListTabulatedFunction(0, 2, new double[]{0, 1, 4});

            System.out.println("   Array функция работает: " + func1.getFunctionValue(1));
            System.out.println("   LinkedList функция работает: " + func2.getFunctionValue(1));
            System.out.println("   Обе функции реализуют один интерфейс!");

        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }
    }
}