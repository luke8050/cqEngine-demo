# cqEngine-demo 项目说明文档

## 项目概述
`cqEngine-demo` 是一个Java项目，主要用于演示如何结合 Ehcache 和 CQEngine 实现数据缓存和索引功能，以提高数据访问效率。该项目通过缓存和索引的结合，减少了对数据库的频繁访问，同时提供了复杂查询的能力。

## 技术栈
- **Ehcache**：一个开源的、基于 Java 的缓存库，用于实现数据的缓存功能。本项目使用 Ehcache 3.10 版本，它提供了灵活的缓存配置和管理功能，包括缓存过期策略、缓存大小限制等。
- **CQEngine**：一个轻量级的 Java 库，用于创建索引和进行复杂查询。CQEngine 可以在内存中创建索引，提高数据查询的性能，特别适用于需要频繁查询的数据。
- **Spring Data JPA**：Spring 框架的一部分，用于简化数据库访问。通过 JPA 接口，我们可以方便地进行数据库的增删改查操作。

## 项目结构
### 主要模块和类
- **`BaseRepository` 接口**：
    - 定义了基础的数据操作方法，包括根据主键查询、获取全部数据、插入、更新和删除等。不同的仓库实现类需要实现这些方法，以提供具体的数据访问功能。
- **`AbstractCacheRepository` 抽象类**：
    - 实现了 `BaseRepository` 接口的部分方法，如 `getAll`、`insert`、`update` 等。同时，提供了缓存初始化的方法 `initializeCache`，在初始化完成后会调用 `afterInitializeCache` 方法，以便进行后续的操作。
- **`NodeCacheRepository` 类**：
    - 继承自 `AbstractCacheRepository`，专门用于处理 `Node` 实体的缓存操作。重写了 `findByKeyId` 和 `deleteByKeyId` 方法，实现了从缓存和数据库中获取和删除数据的逻辑。在 `afterInitializeCache` 方法中注册了事件监听器，用于监听缓存的变化。
- **`IndexedCacheRepository` 类**：
    - 继承自 `AbstractCacheRepository`，并使用`组合设计模式`，引入了 `cqEngineCache`。定义了抽象方法 `createIndex`、`createMetaInfo` 和 `getKeyEqualQuery`，用于创建索引、创建元信息和获取查询条件。在 `afterInitializeCache` 方法中，会初始化索引、加载元信息数据到 CQEngine，并注册事件监听器。
- **`NodeIndexedCacheRepository` 类**：
    - `NodeIndexedCacheRepository` 类主要用于实现基于 CQEngine 的索引缓存功能，通过 CQEngine 的索引来加速查询，它继承自 `IndexedCacheRepository` 类。该类提供了多种方法用于对 Node 实体进行缓存操作，包括根据键查找实体、删除实体、创建索引、创建元信息、获取查询条件以及一些特定的查询方法。
- **`CQEngineCache` 类**：
    - `CQEngineCache` 类通过封装 CQEngine 的核心功能，提供了一个简单易用的缓存接口，支持元素的添加、移除、索引和查询操作。通过使用 CQEngine 的索引机制，能够在内存中高效地处理大量数据的查询，尤其适用于需要频繁进行范围查询和排序的场景。
- **`NodeRepository` 接口**：
    - 继承自 `JpaRepository`，用于操作 `Node` 实体的数据库访问。定义了根据节点 ID 查询和删除节点的方法，方便对 `Node` 实体进行数据库操作。

### 配置文件
- **`application.yml`**：项目的配置文件，注意这里配置的是 `jcache` 而不是 `ehcache`。

## 功能介绍
### 缓存功能
- **数据缓存**：使用 Ehcache 实现数据的缓存，将数据库中的数据加载到缓存中，减少对数据库的频繁访问。例如，在 `NodeCacheRepository` 中，通过 `findByKeyId` 方法，先从缓存中获取实体，如果缓存中不存在，则从数据库中获取并更新缓存。
- **缓存初始化**：在项目启动时，会调用 `initializeCache` 方法，将数据库中的数据加载到缓存中。例如，在 `NodeCacheServiceDemo` 类的 `initializeCache` 方法中，会从数据库加载所有 `Node` 数据到缓存。

### 索引功能
- **索引创建**：使用 CQEngine 创建索引，提高数据查询的性能。在 `IndexedCacheRepository` 中，通过 `createIndex` 方法创建索引。
- **元信息管理**：通过 `createMetaInfo` 方法创建元信息，并将元信息加载到 CQEngine 中。例如，在 `CacheSyncListener` 类中，会根据缓存的变化，同步更新 CQEngine 中的元信息。

### 事件监听
- **缓存事件监听**：通过注册事件监听器，监听缓存的变化，如创建、更新、删除等事件。在 `NodeCacheRepository` 和 `IndexedCacheRepository` 中，都注册了事件监听器，当缓存发生变化时，会触发相应的事件处理逻辑。例如，在 `CacheSyncListener` 类中，会根据缓存的变化，同步更新 CQEngine 中的数据。

## 注意事项
- **监听器注册时机**：`listener` 一定要在 `cache` 加载数据完成之后注册，否则应用启动加载数据过程会打印监听器日志，影响性能和日志的准确性。
- **CacheManager 使用**：`CacheManager` 在 `ehcache` 和 `spring-cache` 中都有，但它们的启动和使用方式不一样。本项目中使用的是 `ehcache` 的实现，需要注意配置和使用方法的差异。

## 参考文档
- [Ehcache 3.10 Documentation 官方](https://www.ehcache.org/documentation/3.10/getting-started.html)：官方文档详细介绍了 Ehcache 3.10 的使用方法和配置方式，包括 xml 和 api 两种配置方式，对应 demo 代码是 `CacheConfig`。
- [CQEngine学习](https://blog.csdn.net/baichoufei90/article/details/83744909)：该文章介绍了 CQEngine 的基本使用和原理，有助于理解项目中 CQEngine 的应用。

## 如何使用
1. **克隆项目**：
    - 使用 `git clone` 命令将项目克隆到本地。
2. **配置环境**：
    - 确保本地安装了 Java 开发环境和 Maven 构建工具。
    - 根据 `application.yml` 配置文件，配置数据库连接等信息。
3. **运行项目**：
    - 使用 Maven 命令 `mvn spring-boot:run` 启动项目。
4. **测试功能**：
    - 可以通过调用 `NodeCacheServiceDemo` 类中的方法，测试缓存和索引功能。例如，调用 `getNodeById` 方法获取节点数据，调用 `saveOrUpdateNode` 方法更新节点数据，调用 `deleteNode` 方法删除节点数据。

## 总结
`cqEngine-demo` 项目通过结合 Ehcache 和 CQEngine，实现了数据缓存和索引功能，提高了数据访问的效率。通过合理的设计和实现，减少了对数据库的频繁访问，同时提供了复杂查询的能力。在使用过程中，需要注意监听器的注册时机和 `CacheManager` 的使用方式，以确保项目的正常运行。