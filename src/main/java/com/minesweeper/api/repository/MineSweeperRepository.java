package com.minesweeper.api.repository;

import com.minesweeper.api.model.MineSweeper;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface MineSweeperRepository extends CrudRepository<MineSweeper, String> {

    List<MineSweeper> getMineSweepersByUserId(String userId);

}
