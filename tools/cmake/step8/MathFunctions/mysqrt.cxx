#include "MathFunctions.h"
#include "TutorialConfig.h"
#include <iostream>

// 引用生成的表
#include "Table.h"

#include <cmath>

double mysqrt(double x)
{
	if (x <= 0)
	{
		return 0;
	}

	// 通过查表来辅助查找一个初值
	double result = x;
	if (x >= 1 && x < 10)
	{
		result = sqrtTable[static_cast<int>(x)];
	}

	// 循环计算十次
	for (int i = 0; i < 10; ++i)
	{
		if (result <= 0)
		{
			result = 0.1;
		}
		double delta = x - (result * result);
		result = result + 0.5 * delta / result;
		std::cout << "Computing sqrt of " << x
				  << " to be " << result << std::endl;
	}

	return result;
}
