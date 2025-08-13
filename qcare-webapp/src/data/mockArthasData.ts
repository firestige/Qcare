// Arthas 模拟数据
import type {
  ArthasStats,
  ArthasInstance,
  ArthasCommand,
} from '../types/arthas';

export const mockArthasStats: ArthasStats = {
  totalInstances: 3,
  runningInstances: 3,
  connectedInstances: 2,
  todayDiagnosticCount: 15,
  reportCount: 8,
};

export const mockArthasInstances: ArthasInstance[] = [
  {
    key: '1',
    name: 'user-service-001',
    pid: '12345',
    status: 'running',
    host: '192.168.1.100',
    port: '8080',
    memory: '512MB',
    cpu: '2.5%',
    arthasStatus: 'connected',
    uptime: '2天3小时',
    jvmVersion: 'OpenJDK 11.0.12',
    mainClass: 'com.icare.user.UserServiceApplication',
  },
  {
    key: '2',
    name: 'order-service-001',
    pid: '12346',
    status: 'running',
    host: '192.168.1.101',
    port: '8081',
    memory: '1GB',
    cpu: '4.2%',
    arthasStatus: 'disconnected',
    uptime: '1天8小时',
    jvmVersion: 'OpenJDK 11.0.12',
    mainClass: 'com.icare.order.OrderServiceApplication',
  },
  {
    key: '3',
    name: 'payment-service-001',
    pid: '12347',
    status: 'running',
    host: '192.168.1.102',
    port: '8082',
    memory: '768MB',
    cpu: '1.8%',
    arthasStatus: 'connected',
    uptime: '5天12小时',
    jvmVersion: 'OpenJDK 11.0.12',
    mainClass: 'com.icare.payment.PaymentServiceApplication',
  },
];

export const mockCommonCommands: ArthasCommand[] = [
  {
    command: 'dashboard',
    description: '显示当前系统的实时数据面板',
    category: 'common',
    examples: ['dashboard', 'dashboard -n 20'],
  },
  {
    command: 'thread',
    description: '查看当前线程信息',
    category: 'common',
    parameters: ['id', 'n', 'b'],
    examples: ['thread', 'thread 1', 'thread -n 3', 'thread -b'],
  },
  {
    command: 'jvm',
    description: '查看当前JVM信息',
    category: 'common',
    examples: ['jvm'],
  },
  {
    command: 'sysprop',
    description: '查看当前JVM的系统属性',
    category: 'common',
    parameters: ['property-name'],
    examples: ['sysprop', 'sysprop java.version'],
  },
  {
    command: 'sysenv',
    description: '查看当前JVM的环境属性',
    category: 'common',
    parameters: ['env-name'],
    examples: ['sysenv', 'sysenv USER'],
  },
  {
    command: 'vmtool',
    description: '利用JVMTI接口，获取内存对象',
    category: 'common',
    parameters: ['action', 'classLoaderClass', 'className'],
    examples: [
      'vmtool --action getInstances --className java.lang.String --limit 10',
    ],
  },
  {
    command: 'getstatic',
    description: '查看类的静态属性',
    category: 'common',
    parameters: ['class-pattern', 'field-pattern'],
    examples: ['getstatic demo.MathGame random'],
  },
  {
    command: 'ognl',
    description: '执行ognl表达式',
    category: 'common',
    parameters: ['express'],
    examples: ['ognl "@System@out.println(\"hello world\")"'],
  },
  {
    command: 'mc',
    description: '内存编译器，编译.java文件为.class文件',
    category: 'common',
    parameters: ['sourcefiles'],
    examples: ['mc /tmp/Test.java'],
  },
  {
    command: 'retransform',
    description: '加载外部的.class文件，retransform jvm已加载的类',
    category: 'common',
    parameters: ['classfiles'],
    examples: ['retransform /tmp/Test.class'],
  },
];

export const mockDiagnosticCommands: ArthasCommand[] = [
  {
    command: 'watch',
    description: '方法执行数据观测',
    category: 'diagnostic',
    parameters: [
      'class-pattern',
      'method-pattern',
      'express',
      'condition-express',
    ],
    examples: [
      'watch demo.MathGame primeFactors returnObj',
      'watch demo.MathGame primeFactors "{params,returnObj}" -x 2',
      'watch demo.MathGame primeFactors "{params[0],target}" "params[0]<0"',
    ],
  },
  {
    command: 'trace',
    description: '方法内部调用路径，并输出方法路径上的每个节点上耗时',
    category: 'diagnostic',
    parameters: ['class-pattern', 'method-pattern', 'condition-express'],
    examples: [
      'trace demo.MathGame run',
      'trace demo.MathGame run "#cost > 10"',
    ],
  },
  {
    command: 'stack',
    description: '输出当前方法被调用的调用路径',
    category: 'diagnostic',
    parameters: ['class-pattern', 'method-pattern', 'condition-express'],
    examples: [
      'stack demo.MathGame primeFactors',
      'stack demo.MathGame primeFactors "params[0]<0" -n 2',
    ],
  },
  {
    command: 'tt',
    description: 'TimeTunnel 记录下指定方法每次调用的入参和返回信息',
    category: 'diagnostic',
    parameters: ['class-pattern', 'method-pattern'],
    examples: ['tt -t demo.MathGame primeFactors', 'tt -l', 'tt -i 1000'],
  },
  {
    command: 'monitor',
    description: '方法执行监控',
    category: 'monitor',
    parameters: ['class-pattern', 'method-pattern'],
    examples: [
      'monitor -c 5 demo.MathGame primeFactors',
      'monitor -c 5 -m 4 demo.MathGame primeFactors',
    ],
  },
  {
    command: 'jad',
    description: '反编译指定已加载类的源码',
    category: 'diagnostic',
    parameters: ['class-pattern'],
    examples: ['jad java.lang.String', 'jad demo.MathGame'],
  },
  {
    command: 'classloader',
    description: '查看classloader的继承树，urls，类加载信息',
    category: 'diagnostic',
    parameters: ['hashcode', 'classLoaderClass'],
    examples: ['classloader', 'classloader -t', 'classloader -l'],
  },
  {
    command: 'heapdump',
    description: '类似jmap命令的heap dump功能',
    category: 'diagnostic',
    parameters: ['file'],
    examples: ['heapdump /tmp/dump.hprof'],
  },
];
