package com.maowei.mall.service.impl;

import com.maowei.mall.consts.MallConst;
import com.maowei.mall.dao.CategoryMapper;
import com.maowei.mall.pojo.Category;
import com.maowei.mall.service.ICategoryService;
import com.maowei.mall.vo.CategoryVo;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        List<CategoryVo> categoryVoList = new ArrayList<>(); // 存放一级父目录
        List<Category> categories = categoryMapper.selectAll(); // 查出mall_categories所有内容

        // 1.查出parent_id=0的数据,查出父目录
        for (Category category : categories) {
            if (category.getParentId().equals(MallConst.ROOT_PARENT_ID)) {
                CategoryVo categoryVo = category2CategoryVo(category);
                categoryVoList.add(categoryVo);
            }
        }
        // 根据sort_order对一级目录排序
        categoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());

        // 2.递归查询子目录
        findSubCategories(categoryVoList, categories);

        return ResponseVo.success(categoryVoList);

        //         lambda表达式 + stream 查询父目录
//        List<CategoryVo> categoryVoList = categories.stream()
//                .filter(e -> e.getParentId().equals(MallConst.ROOT_PARENT_ID))
//                .map(e -> category2CategoryVo(e))
//                .collect(Collectors.toList());

    }

    /**
     * 通过传入的CategoryId查找它的所有次级目录的CategoryId
     * @param id 传入CategoryId
     * @param resultSet 存放某个CategoryId的所有次级目录的CategoryId，
     *                  使用Set是因为Set中的值一定不会重复
     */
    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id, resultSet, categories); // 重载方法，让查询数据库的方法只调用一次
                                                      // 否则每一次递归都要重新查一次数据库，很浪费时间
    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
        for (Category category : categories) {
            if (category.getParentId().equals(id)) {
                resultSet.add(category.getId());
                // 继续查找所有次级目录的CategoryId
                findSubCategoryId(category.getId(), resultSet, categories);
            }
        }
    }

    /**
     * 查询并设置子目录
     * @param categoryVoList 所有同级目录CategoryVo对象的列表
     * @param categories mall_category表单中的所有记录
     */
    public void findSubCategories(List<CategoryVo> categoryVoList, List<Category> categories) {
        if (categoryVoList == null) return;
        for (CategoryVo categoryVo : categoryVoList) {
            List<CategoryVo> subCategoryList = new ArrayList<>();
            for (Category category : categories) {
                // 如果查到子目录，设置子目录，继续往下查
                if (categoryVo.getId().equals(category.getParentId())) {
                    CategoryVo subCategoryVo = category2CategoryVo(category);
                    subCategoryList.add(subCategoryVo);
                }
            }
            // 子目录根据sort_order从大到小排序
            subCategoryList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());

            categoryVo.setSubCategories(subCategoryList);
            findSubCategories(subCategoryList, categories);
        }
    }

    public CategoryVo category2CategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        // categoryVo和category的属性名都相同，可以使用springboot的工具包
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }
}
