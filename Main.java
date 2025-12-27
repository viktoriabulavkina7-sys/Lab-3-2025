import functions.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ ARRAY TABULATED FUNCTION ===");
        testFunction(new ArrayTabulatedFunction(0.1, 10.1, 6));
        System.out.println("\n\n=== ТЕСТИРОВАНИЕ LINKED LIST TABULATED FUNCTION ===");
        testFunction(new LinkedListTabulatedFunction(new double[]{0.1, 2.1, 4.1, 6.1, 8.1, 10.1}, new double[]{0, 0, 0, 0, 0, 0}));
        System.out.println("\n\n=== ТЕСТИРОВАНИЕ ИСКЛЮЧЕНИЙ ===");
        testExceptions();
        System.out.println("\n\n=== ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ ===");
        testAdditionalCases();
    }

    private static void testFunction(TabulatedFunctionImp function) {
        System.out.println("Функция log10(x):");
        System.out.println("Область определения: [" + function.getLeftDomainBorder() + "; " + function.getRightDomainBorder() + "]");
        System.out.println("Количество точек: " + function.getPointsCount());
        System.out.println("Тип функции: " + function.getClass().getSimpleName());

        // Устанавливаем значения y = log10(x)
        System.out.println("\nТочки функции:");
        for (int i = 0; i < function.getPointsCount(); i++) {
            double x = function.getPointX(i);
            double y = Math.log10(x);
            function.setPointY(i, y);
            System.out.printf("Точка %d: (%.1f; %.3f)%n", i + 1, x, y);
        }

        System.out.println("\nЗначения функции в различных точках:");
        double[] testPoints = {-1, 0, 0.1, 0.3, 0.5, 1, 1.5, 2, 3, 5, 7, 10, 10.1, 11};
        for (double x : testPoints) {
            double y = function.getFunctionValue(x);
            if (Double.isNaN(y)) {
                System.out.printf("f(%.1f) = не определено%n", x);
            } else {
                double exact = Math.log10(x);
                System.out.printf("f(%.1f) = %.3f (точное: %.3f)%n", x, y, exact);
            }
        }

        // Тестирование операций с точками
        try {
            System.out.println("\nДобавляем точку (3; 0.477):");
            function.addPoint(new FunctionPoint(3, Math.log10(3)));
            System.out.println("Количество точек после добавления: " + function.getPointsCount());

            System.out.println("\nДобавляем точку (7; 0.845):");
            function.addPoint(new FunctionPoint(7, Math.log10(7)));
            System.out.println("Количество точек после добавления: " + function.getPointsCount());

            System.out.println("\nУдаляем точку с индексом 0:");
            function.deletePoint(0);
            System.out.println("Количество точек после удаления: " + function.getPointsCount());

            System.out.println("\nИтоговая информация о точках:");
            for (int i = 0; i < function.getPointsCount(); i++) {
                double x = function.getPointX(i);
                double y = function.getPointY(i);
                System.out.printf("Точка %d: (%.1f; %.3f)%n", i + 1, x, y);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при работе с точками: " + e.getMessage());
        }
    }

    private static void testExceptions() {
        System.out.println("1. Тестирование недопустимых индексов:");
        try {
            TabulatedFunctionImp func = new ArrayTabulatedFunction(0, 10, 5);
            func.getPoint(10); // Неверный индекс
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n2. Тестирование нарушения порядка X:");
        try {
            TabulatedFunctionImp func = new LinkedListTabulatedFunction(new double[]{0.5, 1.5, 2.5}, new double[]{1, 2, 3});
            func.setPoint(1, new FunctionPoint(0.1, 5));
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n3. Тестирование дублирования точек:");
        try {
            TabulatedFunctionImp func = new ArrayTabulatedFunction(0, 4, 3);
            // Добавляем точку с X=2.0, который уже существует в равномерной сетке [0, 2, 4]
            func.addPoint(new FunctionPoint(2.0, 5.0));
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n4. Тестирование удаления при минимальном количестве точек:");
        try {
            TabulatedFunctionImp func = new LinkedListTabulatedFunction(
                    new double[]{0, 1}, new double[]{0, 1});
            func.deletePoint(0); // Попытка удалить точку когда всего 2 точки
        } catch (IllegalStateException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n5. Тестирование неверных границ области определения:");
        try {
            TabulatedFunctionImp func = new ArrayTabulatedFunction(10, 0, 5); // Левая граница больше правой
        } catch (IllegalArgumentException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n6. Тестирование недостаточного количества точек:");
        try {
            TabulatedFunctionImp func = new LinkedListTabulatedFunction(new double[]{0}, new double[]{0}); // Всего 1 точка
        } catch (IllegalArgumentException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n7. Тестирование установки недопустимого X:");
        try {
            TabulatedFunctionImp func = new ArrayTabulatedFunction(0.5, 2.5, 3);
            func.setPointX(1, 0.1); // X=0.1 меньше предыдущего X=0.5
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n8. Тестирование получения точки с неверным индексом:");
        try {
            TabulatedFunctionImp func = new LinkedListTabulatedFunction(
                    new double[]{0, 1, 2}, new double[]{0, 1, 2});
            func.getPointY(-1); // Отрицательный индекс
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("   Поймано исключение: " + e.getMessage());
        }

        System.out.println("\n9. Тестирование особых случаев вычисления функции:");
        TabulatedFunctionImp func = new ArrayTabulatedFunction(1, 10, 5);
        System.out.println("   f(-5) = " + func.getFunctionValue(-5)); // Вне области определения
        System.out.println("   f(15) = " + func.getFunctionValue(15)); // Вне области определения
    }

    private static void testAdditionalCases() {
        System.out.println("\n1. Тестирование добавления и удаления точки (0,0):");
        try {
            ArrayTabulatedFunction func = new ArrayTabulatedFunction(-2, 3, 6);
            func.addPoint(new FunctionPoint(0, 0));
            System.out.println("   Точка (0,0) успешно добавлена в ArrayTabulatedFunction");
            func.deletePoint(func.getPointsCount() - 1);
            System.out.println("   Последняя точка успешно удалена");
        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }

        System.out.println("\n2. Тестирование добавления и удаления точки (0,0) в LinkedList:");
        try {
            LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                    new double[]{-2, -1, 1, 2}, new double[]{4, 1, 1, 4});
            func.addPoint(new FunctionPoint(0, 0));
            System.out.println("   Точка (0,0) успешно добавлена в LinkedListTabulatedFunction");
            func.deletePoint(func.getPointsCount() - 1);
            System.out.println("   Последняя точка успешно удалена");
        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }

        System.out.println("\n3. Тестирование работы с машинным эпсилон:");
        try {
            ArrayTabulatedFunction func = new ArrayTabulatedFunction(0, 2, 3);
            // Пытаемся добавить точку с x = 1.0 + 1e-10, что почти равно существующей
            func.addPoint(new FunctionPoint(1.0 + 1e-10, 5.0));
            System.out.println("   Точка добавлена (разница больше машинного эпсилона)");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Исключение: " + e.getMessage());
        }
    }
}