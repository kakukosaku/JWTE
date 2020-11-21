# Effective Java

- 创建和销毁对象

1. 用静态工厂代替构造器
2. 遇到多个构造器参数时使用使用构建器(builder)
3. 用私有构造器或者枚举类型强化Single属性
4. 通过私有构造器强化不可实例化的能力
5. 优先考虑依赖注入来引用资源
6. 避免创建不必要的对象
7. 消除过期的对象引用
8. 避免使用终结方法`finalize`和清除方法`clear`
9. try-with-resources优先于try-finally

- 对于所有对象都通用的方法

10. 覆盖equals时请遵守通用约定
11. 覆盖equals时总要覆盖hashCode
12. 始终要覆盖toString
13. 谨慎覆盖clone
14 