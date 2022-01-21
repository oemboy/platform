
package net.platform.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class KafkaAdminService {
    @Autowired
    private KafkaAdmin kafkaAdmin;

    public void createTopic(String topicName, int partition, short replication) {
        NewTopic topic = new NewTopic(topicName, partition, replication);
        this.createTopic(Arrays.asList(topic));
    }

    public void createTopic(List<NewTopic> list) {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            CreateTopicsResult result = client.createTopics(list);
            result.all().get();
            log.info(result.toString());
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException: ", e);
        }
    }

    public void deleteTopics(Collection<String> topicNames) {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DeleteTopicsResult result = client.deleteTopics(topicNames);
            result.all().get();
            log.info(result.toString());
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException: ", e);
        }
    }

    public void describeTopicConfigs(Collection<String> topicNames) {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            List<ConfigResource> configResources = topicNames.stream()
                .map(name -> new ConfigResource(ConfigResource.Type.TOPIC, name))
                .collect(Collectors.toList());
            DescribeConfigsResult result = client.describeConfigs(configResources);
            Map<ConfigResource, Config> configResourceConfigMap = result.all().get();
            log.info(configResourceConfigMap.toString());
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException: ", e);
        }
    }

    public void describeCluster(Boolean includeAuthorized) {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            final DescribeClusterOptions options = new DescribeClusterOptions();
            options.includeAuthorizedOperations(includeAuthorized);

            DescribeClusterResult result = client.describeCluster(options);
            log.info("Cluster id: {}, controller: {}", result.clusterId().get(), result.controller().get());
            for (Node node : result.nodes().get()) {
                log.info(node.toString());

            }
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException: ", e);
        }
    }

    public void listAllTopics(Boolean listInternal) {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            final ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(listInternal);

            ListTopicsResult result = client.listTopics(options);
            log.info("topicNames in cluster:", result.names().get());
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException: ", e);
        }
    }

    public Map<String, TopicDescription> describeTopics(Collection<String> topicNames, Boolean includeAuthorized) {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            final DescribeTopicsOptions options = new DescribeTopicsOptions();
            options.includeAuthorizedOperations(includeAuthorized);

            DescribeTopicsResult result = client.describeTopics(topicNames, options);
            result.all().get().forEach((k, v) -> log.info("{} ===> {}", k, v));
            return result.all().get();
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException: ", e);
        }
        return Collections.emptyMap();
    }

    public Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>
        getOffSet(Map<TopicPartition, OffsetSpec> topicPartitionOffsets) {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            // ListOffsetsOptions options = new ListOffsetsOptions();
            // options.isolationLevel(isolationLevel);
            ListOffsetsResult result = client.listOffsets(topicPartitionOffsets);

            result.all().get().forEach((k, v) -> log.info("{} ===> {}", k, v));
            return result.all().get();
        } catch (InterruptedException e) {
            log.error("InterruptedException: ", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException: ", e);
        }
        return Collections.emptyMap();
    }
}
